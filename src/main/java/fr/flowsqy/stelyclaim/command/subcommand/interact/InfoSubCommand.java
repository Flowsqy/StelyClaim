package fr.flowsqy.stelyclaim.command.subcommand.interact;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.util.PillarCoordinate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class InfoSubCommand extends InteractSubCommand {

    private final String memberPlayerSeparator;
    private final String memberGroupSeparator;
    private final String ownerPlayerSeparator;
    private final String ownerGroupSeparator;
    private final String memberPlayerEmpty;
    private final String memberGroupEmpty;
    private final String ownerPlayerEmpty;
    private final String ownerGroupEmpty;

    public InfoSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        memberPlayerSeparator = messages.getMessage("claim.info.member-player.separator");
        memberGroupSeparator = messages.getMessage("claim.info.member-group.separator");
        ownerPlayerSeparator = messages.getMessage("claim.info.owner-player.separator");
        ownerGroupSeparator = messages.getMessage("claim.info.owner-group.separator");
        memberPlayerEmpty = messages.getMessage("claim.info.member-player.empty");
        memberGroupEmpty = messages.getMessage("claim.info.member-group.empty");
        ownerPlayerEmpty = messages.getMessage("claim.info.owner-player.empty");
        ownerGroupEmpty = messages.getMessage("claim.info.owner-group.empty");
    }

    @Override
    protected boolean interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion, String regionName) {
        String message = messages.getMessage("claim.info.message", "%region%", regionName);
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

        final PillarCoordinate pillarCoordinate = new PillarCoordinate(region, player.getWorld());

        message = replaceSize(message, pillarCoordinate, region);

        message = replacePillar(message, pillarCoordinate);

        player.sendMessage(message);

        return true;
    }

    private String replaceDomainInfo(
            String message,
            DefaultDomain domain,
            String player, String group,
            String memberSeparator, String groupSeparator,
            String emptyPlayer, String emptyGroup
    ){
        final StringBuilder playerBuilder = new StringBuilder();
        final StringBuilder groupBuilder = new StringBuilder();
        getDomainInfo(domain, playerBuilder, groupBuilder, memberSeparator, groupSeparator);
        return message
                .replace(player, playerBuilder.length() == 0 ? emptyPlayer : playerBuilder)
                .replace(group, groupBuilder.length() == 0 ? emptyGroup : groupBuilder);
    }
    
    private void getDomainInfo(DefaultDomain domain, StringBuilder player, StringBuilder group, String memberSeparator, String groupSeparator){
        for(String memberPlayer : domain.getPlayers()){
            if(player.length() > 0){
                player.append(memberSeparator);
            }
            player.append(memberPlayer);
        }
        for(UUID memberUuid : domain.getUniqueIds()){
            if(player.length() > 0){
                player.append(memberSeparator);
            }
            player.append(Bukkit.getOfflinePlayer(memberUuid).getName());
        }
        for(String memberGroup : domain.getGroups()){
            if(group.length() > 0){
                group.append(groupSeparator);
            }
            group.append(memberGroup);
        }
    }

    private String replaceSize(String message, PillarCoordinate pillarCoordinate, ProtectedRegion region){
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

    private String replacePillar(String message, PillarCoordinate pillarCoordinate) {
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
