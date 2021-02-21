package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

public class RemoveOwnerSubCommand extends DomainSubCommand {
    public RemoveOwnerSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected boolean modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion) {
        final DefaultDomain domain = region.getOwners();
        if(!domain.contains(targetPlayer)){
            messages.sendMessage(
                    sender,
                    "claim.notowner" + (ownRegion ? "" : "other"),
                    "%region%", "%target%",
                    region.getId(), targetPlayer
            );
            return false;
        }
        domain.removePlayer(targetPlayer);
        messages.sendMessage(
                sender,
                "claim.removeowner" + (ownRegion ? "" : "other"),
                "%region%", "%target%",
                region.getId(), targetPlayer
        );
        return true;
    }

}
