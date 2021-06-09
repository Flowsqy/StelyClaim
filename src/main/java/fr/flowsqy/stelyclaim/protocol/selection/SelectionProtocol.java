package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.Protocol;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.PillarTextSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SelectionProtocol {

    private final boolean expandRegion;
    private final int maxY;
    private final int minY;
    private final PillarTextSender newPillarTextSender;
    private final PillarTextSender previousPillarTextSender;

    public SelectionProtocol(StelyClaimPlugin plugin) {
        final YamlConfiguration configuration = plugin.getConfiguration();
        expandRegion = configuration.getBoolean("expand-selection-y.expand", false);
        maxY = configuration.getInt("expand-selection-y.max", 255);
        minY = configuration.getInt("expand-selection-y.min", 0);
        newPillarTextSender = new PillarTextSender(plugin.getMessages(), "new", plugin.getPillarData());
        previousPillarTextSender = new PillarTextSender(plugin.getMessages(), "previous", plugin.getPillarData());
    }

    public <T extends ClaimOwner> boolean process(Player sender, ClaimHandler<T> handler, T owner, Protocol protocol) {
        if (protocol != Protocol.DEFINE && protocol != Protocol.REDEFINE) {
            throw new IllegalArgumentException("The protocol must be DEFINE or REDEFINE. (Here it's '" + protocol + "'");
        }

        final ClaimMessage messages = handler.getMessages();
        final LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(sender));
        final com.sk89q.worldedit.world.World world = new BukkitWorld(sender.getWorld());

        final Region selection;
        try {
            selection = session.getSelection(world);
        } catch (IncompleteRegionException exception) {
            messages.sendMessage(sender, "claim.selection.empty");
            return false;
        }

        if (!(selection instanceof CuboidRegion)) {
            messages.sendMessage(sender, "claim.selection.cuboid");
            return false;
        }

        final boolean ownRegion = owner.own(sender);

        if (!ownRegion) {
            if (protocol == Protocol.DEFINE && !sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.DEFINE))) {
                messages.sendMessage(sender, "help.define");
                return false;
            } else if (!sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.REDEFINE))) {
                messages.sendMessage(sender, "help.redefine");
                return false;
            }
        }

        final RegionManager regionManager = RegionFinder.getRegionManager(world, sender, messages);

        final String regionName = RegionFinder.getRegionName(handler, owner);

        final ProtectedRegion region;
        if (protocol == Protocol.DEFINE) {
            if (RegionFinder.mustNotExist(regionManager, regionName, ownRegion, sender, messages)) {
                return false;
            }
            region = null;
        } else {
            region = RegionFinder.mustExist(regionManager, regionName, ownRegion, sender, messages);
            if (region == null) {
                return false;
            }
        }

        if (expandRegion) {
            try {
                final CuboidRegion cuboidSelection = (CuboidRegion) selection;
                selection.expand(
                        BlockVector3.ZERO.withY(maxY - cuboidSelection.getMaximumY()),
                        BlockVector3.ZERO.withY(minY - cuboidSelection.getMinimumY())
                );
            } catch (RegionOperationException e) {
                messages.sendMessage(sender, "util.error", "%error%", "ExpandSelection");
                return false;
            }
        }

        final ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion(regionName,
                selection.getMaximumPoint(),
                selection.getMinimumPoint()
        );

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(newRegion);

        boolean overlapSame = false;
        final StringBuilder builder = new StringBuilder();
        for (ProtectedRegion overlapRegion : intersecting) {
            if (overlapRegion == region) {
                overlapSame = true;
                continue;
            }
            if (builder.length() > 0)
                builder.append(", ");

            builder.append(overlapRegion.getId());
        }

        if (builder.length() != 0) {
            messages.sendMessage(sender, "claim.selection.overlap", "%regions%", builder.toString());
            return false;
        }

        if (protocol == Protocol.DEFINE) {
            handler.getDefineModifier().modify(sender, newRegion, owner);//configModifyRegion(newRegion, "define", player, regionName);

            regionManager.addRegion(newRegion);

            messages.sendMessage(sender, "claim.command.define" + (ownRegion ? "" : "-other"), "%region%", regionName);
        } else {
            if (!overlapSame) {
                messages.sendMessage(sender, "claim.selection.redefinenotoverlap");
            }

            newRegion.copyFrom(region);
            handler.getRedefineModifier().modify(sender, newRegion, owner);//configModifyRegion(newRegion, "redefine", player, regionName);
            regionManager.addRegion(newRegion);

            messages.sendMessage(sender, "claim.command.redefine" + (ownRegion ? "" : "-other"), "%region%", regionName);

            // Previous pillar manage

            previousPillarTextSender.sendMessage(sender, region);
        }

        // Pillars manage

        newPillarTextSender.sendMessage(sender, newRegion);

        // Mail manage

        if (!ownRegion) {
            StelyClaimPlugin.getInstance().getMailManager().sendInfoToTarget(sender, regionName, protocol == Protocol.DEFINE ? "define" : "redefine");
        }

        return true;
    }

}
