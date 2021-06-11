package fr.flowsqy.stelyclaim.command.subcommand.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.protocol.interact.InfoHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoSubCommand extends InteractSubCommand {

    public InfoSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(Player sender, ClaimHandler<T> handler, T owner) {
        return plugin.getProtocolManager().interact(sender.getWorld(), sender, handler, owner, new InfoHandler());
    }

}
