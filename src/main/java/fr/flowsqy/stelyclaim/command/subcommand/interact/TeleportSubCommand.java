package fr.flowsqy.stelyclaim.command.subcommand.interact;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportSubCommand extends InteractSubCommand {

    private final TeleportSync teleportSync;

    public TeleportSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(Player sender, ClaimHandler<T> handler, T owner) {
        return false;
    }

    protected boolean interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion, String regionName) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);

        if (weLoc == null) {
            messages.sendMessage(player, "claim.tp.notset" + (ownRegion ? "" : "-other"), "%region%", regionName);
            return false;
        }

        teleportSync.addTeleport(player, BukkitAdapter.adapt(weLoc));

        messages.sendMessage(player, "claim.command.tp" + (ownRegion ? "" : "-other"), "%region%", regionName);

        return true;
    }

}
