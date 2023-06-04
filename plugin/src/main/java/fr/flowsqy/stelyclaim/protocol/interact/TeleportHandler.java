package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.util.TeleportSync;

public class TeleportHandler implements InteractProtocolHandler {

    private final TeleportSync teleportSync;

    public TeleportHandler(TeleportSync teleportSync) {
        this.teleportSync = teleportSync;
    }

    @Override
    public String getPermission() {
        return ClaimCommand.Permissions.TELEPORT;
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public <T extends ClaimOwner> boolean interactRegion(RegionManager regionManager, ProtectedRegion region, boolean ownRegion, HandledOwner<T> owner, Actor actor, FormattedMessages messages) {
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
