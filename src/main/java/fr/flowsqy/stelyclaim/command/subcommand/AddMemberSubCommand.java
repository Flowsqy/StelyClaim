package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AddMemberSubCommand extends DomainSubCommand {

    public AddMemberSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected void modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion) {
        final DefaultDomain domain = region.getMembers();
        if(domain.contains(targetPlayer))
            messages.sendMessage(
                    sender,
                    "claim.alreadymember" + (ownRegion ? "" : "other"),
                    "%region%", "%target%",
                    region.getId(), targetPlayer
            );
        else{
            domain.addPlayer(targetPlayer);
            messages.sendMessage(
                    sender,
                    "claim.addmember" + (ownRegion ? "" : "other"),
                    "%region%", "%target%",
                    region.getId(), targetPlayer
            );
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}