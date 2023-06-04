package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.protocol.interact.InfoHandler;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class InfoSubCommand extends InteractSubCommand {

    private final static String NAME = "info";
    private final static String[] TRIGGERS = new String[]{NAME, "i"};
    private final ProtocolManager protocolManager;

    public InfoSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS);
        this.protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return protocolManager.interact(world, actor, owner, new InfoHandler());
    }

}
