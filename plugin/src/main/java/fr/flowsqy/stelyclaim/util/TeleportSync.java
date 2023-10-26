package fr.flowsqy.stelyclaim.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TeleportSync {

    private final Plugin plugin;
    private final Map<UUID, TeleportData> pendingTeleportationMap = new HashMap<>();
    private final Lock lock;
    private boolean launched;

    public TeleportSync(@NotNull Plugin plugin) {
        this.plugin = plugin;
        lock = new ReentrantLock();
    }

    public void addTeleport(@NotNull Entity entity, @NotNull Location location) {
        try {
            lock.lock();
            pendingTeleportationMap.put(entity.getUniqueId(), new TeleportData(entity, location));
            if (!launched) {
                launchTask();
            }
        } finally {
            lock.unlock();
        }
    }

    private void launchTask() {
        launched = true;
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                lock.lock();
                teleportTask();
            } finally {
                lock.unlock();
            }
            launched = false;
        });
    }

    private void teleportTask() {
        for (TeleportData teleportData : pendingTeleportationMap.values()) {
            teleportData.entity().teleport(teleportData.location());
        }
        pendingTeleportationMap.clear();
        launched = false;
    }

    private record TeleportData(@NotNull Entity entity, @NotNull Location location) {
    }

}
