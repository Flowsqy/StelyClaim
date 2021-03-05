package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.entity.Player;

public class TeleportSubCommand extends InteractSubCommand {

    private final TeleportSync teleportSync;

    public TeleportSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    protected void interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);

        if(weLoc == null){
            messages.sendMessage(player, "claim.notp" + (ownRegion ? "" : "other"), "%region%", region.getId());
            return;
        }

        teleportSync.addTeleport(player, BukkitAdapter.adapt(weLoc));

        messages.sendMessage(player, "claim.tp" + (ownRegion ? "" : "other"), "%region%", region.getId());
    }

}
