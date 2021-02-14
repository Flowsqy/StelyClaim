package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.util.PillarData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SelectionSubCommand extends RegionSubCommand {

    private final SessionManager sessionManager;
    //TODO Initialize this with the config
    private final int maxY = 255;
    private final int minY = 0;
    private final Map<String, PillarData> pillarData;

    public SelectionSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
        this.sessionManager = plugin.getSessionManager();
        this.pillarData = plugin.getPillarData();
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if(size != 1 && size != 2){
            messages.sendMessage(sender,
                    "help."
                            + getName()
                            + (sender.hasPermission(getPermission()+"-other") ? "-other" : "")
            );
            return;
        }
        final Player player = (Player) sender;

        final LocalSession session = sessionManager.get(BukkitAdapter.adapt(player));
        final World world = new BukkitWorld(player.getWorld());

        final Region selection;
        try {
            selection = session.getSelection(world);
        }catch (IncompleteRegionException exception){
            messages.sendMessage(player, "claim.noselection");
            return;
        }

        if(!(selection instanceof CuboidRegion)){
            messages.sendMessage(player, "claim.notcuboid");
            return;
        }

        final String regionName;
        final boolean ownRegion;
        if(size == 1){
            regionName = player.getName();
            ownRegion = true;
        }
        else{
            regionName = args.get(1);
            ownRegion = regionName.equalsIgnoreCase(player.getName());
        }

        if(!ownRegion && !player.hasPermission(getPermission()+"-other")){
            messages.sendMessage(player, "help."+getPermission());
            return;
        }

        final RegionManager regionManager = getRegionContainer(world);
        if(regionManager == null){
            messages.sendMessage(player,
                    "claim.worldnothandle",
                    "%world%", world.getName());
            return;
        }

        final ProtectedRegion region = regionManager.getRegion(regionName);
        if(checkExistRegion(region != null, player, ownRegion, regionName, world.getName())){
            return;
        }

        try {
            final CuboidRegion cuboidSelection = (CuboidRegion) selection;
            selection.expand(
                    BlockVector3.ZERO.withY(maxY-cuboidSelection.getMaximumY()),
                    BlockVector3.ZERO.withY(minY-cuboidSelection.getMinimumY())
            );
        } catch (RegionOperationException e) {
            messages.sendMessage(player, "util.error", "%error%", "ExtendSelection");
            return;
        }

        final ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion(regionName,
                selection.getMaximumPoint(),
                selection.getMinimumPoint()
        );

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(newRegion);

        boolean overlapSame = false;
        final StringBuilder builder = new StringBuilder();
        for(ProtectedRegion overlapRegion : intersecting){
            if(overlapRegion == region) {
                overlapSame = true;
                continue;
            }
            if(builder.length() != 0)
                builder.append(", ");

            builder.append(overlapRegion.getId());
        }

        if(builder.length() != 0){
            messages.sendMessage(player, "claim.overlap", "%regions%", builder.toString());
            return;
        }

        checkIntegrateRegion(overlapSame, player);

        manageRegion(player, region, newRegion, ownRegion, regionManager);

        // Pillars manage
        PillarData pillarData = this.pillarData.get(sender.getName());
        if(pillarData == null){
            pillarData = new PillarData();
            this.pillarData.put(sender.getName(), pillarData);
        }
        final BlockVector3 maxPoint = newRegion.getMaximumPoint();
        final BlockVector3 minPoint = newRegion.getMinimumPoint();

        final int maxX = Math.max(maxPoint.getBlockX(), minPoint.getBlockX());
        final int minX = Math.min(maxPoint.getBlockX(), minPoint.getBlockX());
        final int maxZ = Math.max(maxPoint.getBlockZ(), minPoint.getBlockZ());
        final int minZ = Math.min(maxPoint.getBlockZ(), minPoint.getBlockZ());

        final org.bukkit.World pillarWorld = player.getWorld();
        final Location pillarNW = new Location(pillarWorld, minX, 0, minZ, -45, 0);
        final Location pillarNE = new Location(pillarWorld, maxX, 0, minZ, 45, 0);
        final Location pillarSW = new Location(pillarWorld, minX, 0, maxZ, -135, 0);
        final Location pillarSE = new Location(pillarWorld, maxX, 0, maxZ, 135, 0);
        final Location pillarCurrent = player.getLocation();

        final String idNW = pillarData.registerLocation(pillarNW);
        final String idNE = pillarData.registerLocation(pillarNE);
        final String idSW = pillarData.registerLocation(pillarSW);
        final String idSE = pillarData.registerLocation(pillarSE);
        final String idCurrent = pillarData.registerLocation(pillarCurrent);

        player.spigot().sendMessage(createTextComponent("nw", idNW));
        player.spigot().sendMessage(createTextComponent("ne", idNE));
        player.spigot().sendMessage(createTextComponent("sw", idSW));
        player.spigot().sendMessage(createTextComponent("se", idSE));
        player.spigot().sendMessage(createTextComponent("current", idCurrent));

        //TODO Mail gestion

    }

    protected abstract boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName);

    protected void checkIntegrateRegion(boolean overlapSame, Player player){}

    protected abstract void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager);

    private TextComponent createTextComponent(String direction, String id){
        // TODO Link message to the config
        final TextComponent textComponent = new TextComponent();
        textComponent.setExtra(new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText("Coucou je suis le pillier "+direction))));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Clique moi dessus")));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim pillar " + id));
        return textComponent;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if(sender.hasPermission(getPermission()+"-other") && args.size() == 2){
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .filter(((Player) sender)::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        else
            return Collections.emptyList();
    }

}
