package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.protocol.context.InteractContext;
import fr.flowsqy.stelyclaim.pillar.cuboid.CuboidPillarCoordinate;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InfoHandler implements InteractProtocolHandler {

    @Override
    public void interactRegion(@NotNull RegionManager regionManager, @NotNull ProtectedRegion region, @NotNull ActionContext context) {
        /*
        final String memberPlayerSeparator = messages.getFormattedMessage("claim.info.member-player.separator");
        final String memberGroupSeparator = messages.getFormattedMessage("claim.info.member-group.separator");
        final String ownerPlayerSeparator = messages.getFormattedMessage("claim.info.owner-player.separator");
        final String ownerGroupSeparator = messages.getFormattedMessage("claim.info.owner-group.separator");
        final String memberPlayerEmpty = messages.getFormattedMessage("claim.info.member-player.empty");
        final String memberGroupEmpty = messages.getFormattedMessage("claim.info.member-group.empty");
        final String ownerPlayerEmpty = messages.getFormattedMessage("claim.info.owner-player.empty");
        final String ownerGroupEmpty = messages.getFormattedMessage("claim.info.owner-group.empty");

        String message = messages.getFormattedMessage("claim.info.message", "%region%", owner.owner().getName());
        message = replaceDomainInfo(
                message,
                region.getOwners(),
                "%owner-players%", "%owner-groups%",
                ownerPlayerSeparator, ownerGroupSeparator,
                ownerPlayerEmpty, ownerGroupEmpty
        );
        message = replaceDomainInfo(
                message,
                region.getMembers(),
                "%member-players%", "%member-groups%",
                memberPlayerSeparator, memberGroupSeparator,
                memberPlayerEmpty, memberGroupEmpty
        );

        if (!actor.isPlayer()) {
            return true;
        }

        final PillarCoordinate pillarCoordinate = new PillarCoordinate(region, actor.getPlayer().getWorld());
        message = replaceSize(message, pillarCoordinate, region);
        message = replacePillar(message, pillarCoordinate);
        actor.getBukkit().sendMessage(message);

        //return true;
        */
        final InteractContext interactContext = context.getCustomData(InteractContext.class);
        final String handledOwner = interactContext.getOwnerContext().getLazyHandledOwner().toHandledOwner().toString();
        context.getActor().getBukkit().spigot().sendMessage(new TextComponent(handledOwner));
        context.setResult(new ActionResult(InteractProtocol.SUCCESS, true));
    }

    private String replaceDomainInfo(
            String message,
            DefaultDomain domain,
            String player, String group,
            String memberSeparator, String groupSeparator,
            String emptyPlayer, String emptyGroup
    ) {
        final StringBuilder playerBuilder = new StringBuilder();
        final StringBuilder groupBuilder = new StringBuilder();
        getDomainInfo(domain, playerBuilder, groupBuilder, memberSeparator, groupSeparator);
        return message
                .replace(player, playerBuilder.length() == 0 ? emptyPlayer : playerBuilder)
                .replace(group, groupBuilder.length() == 0 ? emptyGroup : groupBuilder);
    }

    private void getDomainInfo(DefaultDomain domain, StringBuilder player, StringBuilder group, String memberSeparator, String groupSeparator) {
        for (String memberPlayer : domain.getPlayers()) {
            if (player.length() > 0) {
                player.append(memberSeparator);
            }
            player.append(memberPlayer);
        }
        for (UUID memberUuid : domain.getUniqueIds()) {
            if (player.length() > 0) {
                player.append(memberSeparator);
            }
            player.append(Bukkit.getOfflinePlayer(memberUuid).getName());
        }
        for (String memberGroup : domain.getGroups()) {
            if (group.length() > 0) {
                group.append(groupSeparator);
            }
            group.append(memberGroup);
        }
    }

    private String replaceSize(String message, CuboidPillarCoordinate pillarCoordinate, ProtectedRegion region) {
        final int xSize = pillarCoordinate.getMaxX() - pillarCoordinate.getMinX() + 1;
        final int zSize = pillarCoordinate.getMaxZ() - pillarCoordinate.getMinZ() + 1;

        final int maxY = region.getMaximumPoint().getBlockY();
        final int minY = region.getMinimumPoint().getBlockY();
        final int ySize = ((maxY > minY) ? (maxY - minY) : (minY - maxY)) + 1;

        return message
                .replace("%x-size%", String.valueOf(xSize))
                .replace("%y-size%", String.valueOf(ySize))
                .replace("%z-size%", String.valueOf(zSize));
    }

    private String replacePillar(String message, CuboidPillarCoordinate pillarCoordinate) {
        final Location northwest = pillarCoordinate.getNorthWestBlockLocation();
        final Location northeast = pillarCoordinate.getNorthEastBlockLocation();
        final Location southwest = pillarCoordinate.getSouthWestBlockLocation();
        final Location southeast = pillarCoordinate.getSouthEastBlockLocation();

        return message
                .replace("%northwest-x%", String.valueOf(northwest.getBlockX()))
                .replace("%northwest-z%", String.valueOf(northwest.getBlockZ()))
                .replace("%northeast-x%", String.valueOf(northeast.getBlockX()))
                .replace("%northeast-z%", String.valueOf(northeast.getBlockZ()))
                .replace("%southwest-x%", String.valueOf(southwest.getBlockX()))
                .replace("%southwest-z%", String.valueOf(southwest.getBlockZ()))
                .replace("%southeast-x%", String.valueOf(southeast.getBlockX()))
                .replace("%southeast-z%", String.valueOf(southeast.getBlockZ()));
    }

}
