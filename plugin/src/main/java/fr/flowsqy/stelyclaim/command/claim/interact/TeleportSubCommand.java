package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.protocol.interact.TeleportHandler;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TeleportSubCommand extends InteractSubCommand {

    private final static String NAME = "teleport";
    private final static String[] TRIGGERS = new String[]{NAME, "tp"};
    private final ProtocolManager protocolManager;
    private final TeleportSync teleportSync;

    public TeleportSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS);
        protocolManager = plugin.getProtocolManager();
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return protocolManager.interact(world, actor, owner, new TeleportHandler(teleportSync));
    }

}
