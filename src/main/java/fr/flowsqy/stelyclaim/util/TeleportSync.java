package fr.flowsqy.stelyclaim.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class TeleportSync {

    private final Plugin plugin;
    private final Map<Player, Location> teleportLocation = new HashMap<>();
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private boolean launched;

    public TeleportSync(Plugin plugin) {
        this.plugin = plugin;
    }

    public void addTeleport(Player player, Location location) {
        teleportLocation.put(player, location);
        if (!launched) {
            scheduler.runTask(plugin, () -> {
                for (Map.Entry<Player, Location> entry : teleportLocation.entrySet()) {
                    entry.getKey().teleport(entry.getValue());
                }
                launched = false;
            });
            launched = true;
        }
    }

}
