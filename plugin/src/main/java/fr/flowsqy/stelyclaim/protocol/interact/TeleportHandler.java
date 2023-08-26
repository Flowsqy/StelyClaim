package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.jetbrains.annotations.NotNull;

public class TeleportHandler implements InteractProtocolHandler {

    public static final int TP_NOT_SET;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            TP_NOT_SET = register.get();
        } finally {
            register.unlock();
        }
    }

    private final TeleportSync teleportSync;

    public TeleportHandler(TeleportSync teleportSync) {
        this.teleportSync = teleportSync;
    }

    @Override
    public void interactRegion(@NotNull RegionManager regionManager, @NotNull ProtectedRegion region, @NotNull ActionContext context) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);


        if (weLoc == null) {
            context.setResult(new ActionResult(TP_NOT_SET, false));
            //messages.sendMessage(actor.getBukkit(), "claim.tp.notset" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());
            return;
        }

        final Actor actor = context.getActor();
        if (!actor.isMovable()) {
            throw new RuntimeException("Actor is not movable");
        }
        actor.getMovable().setLocation(teleportSync, BukkitAdapter.adapt(weLoc));
        //messages.sendMessage(actor.getBukkit(), "claim.command.tp" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());
        context.setResult(new ActionResult(InteractProtocol.SUCCESS, true));
    }

}
