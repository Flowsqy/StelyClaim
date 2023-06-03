package fr.flowsqy.stelyclaim.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TeleportSync {

    private final Plugin plugin;
    private final Map<Entity, Location> teleportLocation = new HashMap<>();
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private boolean launched;

    public TeleportSync(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void addTeleport(@NotNull Entity player, @NotNull Location location) {
        teleportLocation.put(player, location);
        if (!launched) {
            scheduler.runTask(plugin, () -> {
                for (Map.Entry<Entity, Location> entry : teleportLocation.entrySet()) {
                    entry.getKey().teleport(entry.getValue());
                }
                teleportLocation.clear();
                launched = false;
            });
            launched = true;
        }
    }

}
