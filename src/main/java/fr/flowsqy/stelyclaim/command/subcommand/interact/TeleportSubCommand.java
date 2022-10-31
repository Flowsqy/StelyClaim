package fr.flowsqy.stelyclaim.command.subcommand.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.protocol.interact.TeleportHandler;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportSubCommand extends InteractSubCommand {

    private final TeleportSync teleportSync;

    public TeleportSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        this.teleportSync = plugin.getTeleportSync();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(Player sender, ClaimHandler<T> handler, T owner) {
        return protocolManager.interact(sender.getWorld(), sender, handler, owner, new TeleportHandler(teleportSync));
    }

}
