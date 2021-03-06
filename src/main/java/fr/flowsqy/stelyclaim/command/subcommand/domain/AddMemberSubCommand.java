package fr.flowsqy.stelyclaim.command.subcommand.domain;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class AddMemberSubCommand extends DomainSubCommand {

    public AddMemberSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected boolean modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion, String regionName) {
        final DefaultDomain domain = region.getMembers();
        if(domain.contains(targetPlayer)) {
            messages.sendMessage(
                    sender,
                    "claim.domain.member.already" + (ownRegion ? "" : "-other"),
                    "%region%", "%target%",
                    regionName, targetPlayer
            );
            return false;
        }
        domain.addPlayer(targetPlayer);
        messages.sendMessage(
                sender,
                "claim.command.addmember" + (ownRegion ? "" : "-other"),
                "%region%", "%target%",
                regionName, targetPlayer
        );
        return true;
    }

}
