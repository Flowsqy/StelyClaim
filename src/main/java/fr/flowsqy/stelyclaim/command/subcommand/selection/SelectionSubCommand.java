package fr.flowsqy.stelyclaim.command.subcommand.selection;

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
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.RegionSubCommand;
import fr.flowsqy.stelyclaim.util.PillarData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SelectionSubCommand extends RegionSubCommand {

    private final SessionManager sessionManager;
    private final boolean expandRegion;
    private final int maxY;
    private final int minY;
    private final Map<String, PillarData> pillarData;

    private final String pillarMessage;
    private final TextComponent pillarNWTxtCpnt;
    private final TextComponent pillarNETxtCpnt;
    private final TextComponent pillarSWTxtCpnt;
    private final TextComponent pillarSETxtCpnt;
    private final TextComponent pillarCurrentTxtCpnt;

    public SelectionSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
        this.sessionManager = plugin.getSessionManager();
        this.pillarData = plugin.getPillarData();

        final Configuration configuration = plugin.getConfiguration();
        expandRegion = configuration.getBoolean("expand-selection-y.expand", false);
        maxY = configuration.getInt("expand-selection-y.max", 255);
        minY = configuration.getInt("expand-selection-y.min", 0);

        pillarMessage = messages.getMessage("pillar.message");
        if(pillarMessage != null) {
            pillarNWTxtCpnt = createTextComponent("northwest");
            pillarNETxtCpnt = createTextComponent("northeast");
            pillarSWTxtCpnt = createTextComponent("southwest");
            pillarSETxtCpnt = createTextComponent("southeast");
            pillarCurrentTxtCpnt = createTextComponent("current");
        }
        else{
            pillarNWTxtCpnt = null;
            pillarNETxtCpnt = null;
            pillarSWTxtCpnt = null;
            pillarSETxtCpnt = null;
            pillarCurrentTxtCpnt = null;
        }
    }

    private TextComponent createTextComponent(String direction){
        final String message = messages.getMessage("pillar."+direction+".message");
        final TextComponent textComponent = new TextComponent();
        if(message == null)
            return textComponent;
        textComponent.setExtra(
                new ArrayList<>(Arrays.asList(
                        TextComponent.fromLegacyText(message)
                ))
        );
        final String text = messages.getMessage("pillar."+direction+".hover");
        if(text != null){
            textComponent.setHoverEvent(
                    new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new Text(text)
                    )
            );
        }
        return textComponent;
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

        if(expandRegion) {
            try {
                final CuboidRegion cuboidSelection = (CuboidRegion) selection;
                selection.expand(
                        BlockVector3.ZERO.withY(maxY - cuboidSelection.getMaximumY()),
                        BlockVector3.ZERO.withY(minY - cuboidSelection.getMinimumY())
                );
            } catch (RegionOperationException e) {
                messages.sendMessage(player, "util.error", "%error%", "ExpandSelection");
                return;
            }
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

        if(pillarMessage != null) {
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

            final ComponentReplacer replacer = new ComponentReplacer(pillarMessage);

            if (pillarNWTxtCpnt != null) {
                final Location pillar = new Location(pillarWorld, minX, 0, minZ, -45, 0);
                sendPillarMessage("%northwest%", pillarNWTxtCpnt, pillar, pillarData, replacer);
            }
            if (pillarNETxtCpnt != null) {
                final Location pillar = new Location(pillarWorld, maxX, 0, minZ, 45, 0);
                sendPillarMessage("%northeast%", pillarNETxtCpnt, pillar, pillarData, replacer);
            }
            if (pillarSWTxtCpnt != null) {
                final Location pillar = new Location(pillarWorld, minX, 0, maxZ, -135, 0);
                sendPillarMessage("%southwest%", pillarSWTxtCpnt, pillar, pillarData, replacer);
            }
            if (pillarSETxtCpnt != null) {
                final Location pillar = new Location(pillarWorld, maxX, 0, maxZ, 135, 0);
                sendPillarMessage("%southeast%", pillarSETxtCpnt, pillar, pillarData, replacer);
            }
            if (pillarCurrentTxtCpnt != null) {
                sendPillarMessage("%current%", pillarCurrentTxtCpnt, player.getLocation(), pillarData, replacer);
            }

            player.spigot().sendMessage(replacer.create());
        }

        // Mail manage

        if(!ownRegion){
            plugin.getMailManager().sendInfoToTarget(player, regionName, getName());
        }

    }

    protected abstract boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName);

    protected void checkIntegrateRegion(boolean overlapSame, Player player){}

    protected abstract void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager);

    private void sendPillarMessage(String regex, TextComponent textComponent, Location location, PillarData pillarData, ComponentReplacer replacer){
        final String id = pillarData.registerLocation(location);
        final TextComponent component = textComponent.duplicate();
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim pillar " + id));
        replacer.replace(regex, new TextComponent[]{component});
    }

    protected final void configModifyRegion(ProtectedRegion newRegion, String category, Player sender){
        final Configuration config = plugin.getConfiguration();
        final String setTp = config.getString(category + ".set-tp");
        if(setTp != null){
            final Location location;
            final BlockVector3 maxPoint = newRegion.getMaximumPoint();
            final BlockVector3 minPoint = newRegion.getMinimumPoint();

            final int maxX = Math.max(maxPoint.getBlockX(), minPoint.getBlockX());
            final int minX = Math.min(maxPoint.getBlockX(), minPoint.getBlockX());
            final int maxZ = Math.max(maxPoint.getBlockZ(), minPoint.getBlockZ());
            final int minZ = Math.min(maxPoint.getBlockZ(), minPoint.getBlockZ());

            final org.bukkit.World world = sender.getWorld();
            final boolean pillar;
            switch (setTp) {
                case "here":
                    location = sender.getLocation();
                    pillar = false;
                    break;
                case "northwest":
                    location = new Location(world, minX, 0, minZ, -45, 0);
                    pillar = true;
                    break;
                case "northeast":
                    location = new Location(world, maxX, 0, minZ, 45, 0);
                    pillar = true;
                    break;
                case "southwest":
                    location = new Location(world, minX, 0, maxZ, -135, 0);
                    pillar = true;
                    break;
                case "southeast":
                    location = new Location(world, maxX, 0, maxZ, 135, 0);
                    pillar = true;
                    break;
                default:
                    location = null;
                    pillar = false;
            }
            if(pillar)
                location.add(0.5, world.getHighestBlockYAt(location) + 3, 0.5);

            if(location != null)
                newRegion.setFlag(Flags.TELE_LOC, BukkitAdapter.adapt(location));
        }

        final List<String> owners = config.getStringList(category+".owner");
        final List<String> ownerGroups = config.getStringList(category+".owner-group");
        if(!owners.isEmpty() || !ownerGroups.isEmpty()){
            final DefaultDomain ownerDomain = newRegion.getOwners();
            for(String owner : owners)
                ownerDomain.addPlayer(
                        owner
                                .replace("%sender%", sender.getName())
                                .replace("%target%", newRegion.getId())
                );
            for(String groupOwner : ownerGroups)
                ownerDomain.addGroup(groupOwner);
        }
        final List<String> members = config.getStringList(category+".member");
        final List<String> memberGroups = config.getStringList(category+".member-group");
        if(!members.isEmpty() || !memberGroups.isEmpty()){
            final DefaultDomain memberDomain = newRegion.getMembers();
            for(String member : members)
                memberDomain.addPlayer(
                        member
                                .replace("%sender%", sender.getName())
                                .replace("%target%", newRegion.getId())
                );
            for(String memberOwner : memberGroups)
                memberDomain.addGroup(memberOwner);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if(sender.hasPermission(getPermission()+"-other") && args.size() == 2){
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            final Player player = (Player) sender;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        else
            return Collections.emptyList();
    }

}
