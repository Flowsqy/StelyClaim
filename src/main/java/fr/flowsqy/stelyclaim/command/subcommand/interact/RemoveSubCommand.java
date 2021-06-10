package fr.flowsqy.stelyclaim.command.subcommand.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveSubCommand extends InteractSubCommand {

    public RemoveSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(Player sender, ClaimHandler<T> handler, T owner) {
        return StelyClaimPlugin.getInstance().getProtocolManager().remove(sender.getWorld(), sender, handler, owner);
    }

}
