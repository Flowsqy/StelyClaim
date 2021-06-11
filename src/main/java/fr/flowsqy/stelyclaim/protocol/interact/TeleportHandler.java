package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import org.bukkit.entity.Player;

public class TeleportHandler implements InteractProtocolHandler {

    @Override
    public String getPermission() {
        return ClaimCommand.Permissions.TELEPORT;
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public <T extends ClaimOwner> boolean interactRegion(RegionManager regionManager, ProtectedRegion region, boolean ownRegion, ClaimHandler<T> handler, T owner, Player sender, ClaimMessage messages) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);

        if (weLoc == null) {
            messages.sendMessage(sender, "claim.tp.notset" + (ownRegion ? "" : "-other"), "%region%", owner.getName());
            return false;
        }

        StelyClaimPlugin.getInstance().getTeleportSync().addTeleport(sender, BukkitAdapter.adapt(weLoc));

        messages.sendMessage(sender, "claim.command.tp" + (ownRegion ? "" : "-other"), "%region%", owner.getName());

        return true;
    }
}
