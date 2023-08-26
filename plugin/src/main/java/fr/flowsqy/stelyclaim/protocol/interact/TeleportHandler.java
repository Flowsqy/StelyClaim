package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.jetbrains.annotations.NotNull;

public class TeleportHandler implements InteractProtocolHandler {

    private final TeleportSync teleportSync;

    public TeleportHandler(TeleportSync teleportSync) {
        this.teleportSync = teleportSync;
    }

    @Override
    public void interactRegion(@NotNull RegionManager regionManager, @NotNull ProtectedRegion region, @NotNull ActionContext<ClaimContext> context) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);

        if (weLoc == null) {
            messages.sendMessage(actor.getBukkit(), "claim.tp.notset" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());
            return false;
        }

        if (!actor.isMovable()) {
            return false;
        }
        actor.getMovable().setLocation(teleportSync, BukkitAdapter.adapt(weLoc));
        messages.sendMessage(actor.getBukkit(), "claim.command.tp" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());
        return true;
    }

}
