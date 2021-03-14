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
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.RegionSubCommand;
import fr.flowsqy.stelyclaim.util.PillarCoordinate;
import fr.flowsqy.stelyclaim.util.PillarTextSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class SelectionSubCommand extends RegionSubCommand {

    private final SessionManager sessionManager;
    private final boolean expandRegion;
    private final int maxY;
    private final int minY;

    private final PillarTextSender newPillarTxtSender;

    public SelectionSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        this.sessionManager = plugin.getSessionManager();

        final Configuration configuration = plugin.getConfiguration();
        expandRegion = configuration.getBoolean("expand-selection-y.expand", false);
        maxY = configuration.getInt("expand-selection-y.max", 255);
        minY = configuration.getInt("expand-selection-y.min", 0);

        this.newPillarTxtSender = new PillarTextSender(messages, "new", plugin.getPillarData());
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
            messages.sendMessage(player, "claim.selection.empty");
            return;
        }

        if(!(selection instanceof CuboidRegion)){
            messages.sendMessage(player, "claim.selection.cuboid");
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
            messages.sendMessage(player, "help."+getName());
            return;
        }

        final RegionManager regionManager = getRegionManager(world);
        if(regionManager == null){
            messages.sendMessage(player,
                    "claim.world.nothandle",
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
            if(builder.length() > 0)
                builder.append(", ");

            builder.append(overlapRegion.getId());
        }

        if(builder.length() != 0){
            messages.sendMessage(player, "claim.selection.overlap", "%regions%", builder.toString());
            return;
        }

        checkIntegrateRegion(overlapSame, player);

        manageRegion(player, region, newRegion, ownRegion, regionManager, regionName);

        // Pillars manage

        newPillarTxtSender.sendMessage(player, newRegion);

        // Mail manage

        if(!ownRegion){
            plugin.getMailManager().sendInfoToTarget(player, regionName, getName());
        }

    }

    protected abstract boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName);

    protected void checkIntegrateRegion(boolean overlapSame, Player player){}

    protected abstract void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager, String regionName);

    protected final void configModifyRegion(ProtectedCuboidRegion newRegion, String category, Player sender, String regionNameWithCase){
        final Configuration config = plugin.getConfiguration();

        // Teleportation flag
        final String setTp = config.getString(category + ".set-tp");
        if(setTp != null){
            final PillarCoordinate pillarCoordinate = new PillarCoordinate(newRegion, sender.getWorld());
            final Location location;

            switch (setTp) {
                case "here":
                    location = sender.getLocation();
                    break;
                case "northwest":
                    location = pillarCoordinate.getNorthWestLocation();
                    break;
                case "northeast":
                    location = pillarCoordinate.getNorthEastLocation();
                    break;
                case "southwest":
                    location = pillarCoordinate.getSouthWestLocation();
                    break;
                case "southeast":
                    location = pillarCoordinate.getSouthEastLocation();
                    break;
                default:
                    location = null;
            }

            if(location != null)
                newRegion.setFlag(Flags.TELE_LOC, BukkitAdapter.adapt(location));
        }


        // Owners
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

        // Members
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

        // Greeting flag
        final String greeting = messages.getMessage(
                "claim.selection-flags."+category+".greeting",
                "%region%", regionNameWithCase
        );
        newRegion.setFlag(Flags.GREET_MESSAGE, greeting);

        // Farewell flag
        final String farewell = messages.getMessage(
                "claim.selection-flags."+category+".farewell",
                "%region%", regionNameWithCase
        );
        newRegion.setFlag(Flags.FAREWELL_MESSAGE, farewell);

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
