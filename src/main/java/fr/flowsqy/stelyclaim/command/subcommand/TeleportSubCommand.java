package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class TeleportSubCommand extends InteractSubCommand {

    private final TeleportAsync teleportAsync;

    public TeleportSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
        teleportAsync = new TeleportAsync();
    }

    @Override
    protected void interactRegion(Player player, ProtectedRegion region, boolean ownRegion) {
        final com.sk89q.worldedit.util.Location weLoc = region.getFlag(Flags.TELE_LOC);

        if(weLoc == null){
            messages.sendMessage(player, "claim.notp" + (ownRegion ? "" : "other"), "%region%", region.getId());
            return;
        }

        teleportAsync.addTeleport(player, BukkitAdapter.adapt(weLoc));

        messages.sendMessage(player, "claim.tp" + (ownRegion ? "" : "other"), "%region%", region.getId());
    }

    private final class TeleportAsync {

        private final Map<Player, Location> teleportLocation = new HashMap<>();
        private final BukkitScheduler scheduler = Bukkit.getScheduler();

        private boolean launched;

        public void addTeleport(Player player, Location location){
            teleportLocation.put(player, location);
            if(!launched){
                scheduler.runTask(plugin, () -> {
                    for(Map.Entry<Player, Location> entry : teleportLocation.entrySet()){
                        entry.getKey().teleport(entry.getValue());
                    }
                    launched = false;
                });
                launched = true;
            }
        }

    }

}
