package fr.flowsqy.stelyclaim.io;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StatisticManager {

    private final Plugin plugin;
    private final File file;
    private final YamlConfiguration config;
    private final Set<String> commands;
    private final Map<UUID, Map<String, Integer>> data;
    private final Lock lock;
    private boolean launch = false;

    public StatisticManager(@NotNull Plugin plugin, @NotNull File dataFolder) {
        this.plugin = plugin;
        file = new File(dataFolder, "statistics.yml");
        config = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        commands = new HashSet<>();
        data = new HashMap<>();
        lock = new ReentrantLock();
    }

    public void initData() {
        try {
            lock.lock();
            data.clear();
            for (Map.Entry<String, Object> playerEntry : config.getValues(false).entrySet()) {
                final UUID playerId;
                try {
                    playerId = UUID.fromString(playerEntry.getKey());
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
                if (!(playerEntry.getValue() instanceof ConfigurationSection section)) {
                    continue;
                }
                final Map<String, Integer> playerStats = new HashMap<>();
                for (Map.Entry<String, Object> commandEntry : section.getValues(false).entrySet()) {
                    final int value;
                    try {
                        value = Integer.parseInt(commandEntry.getValue().toString());
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    playerStats.put(commandEntry.getKey(), value);
                }
                if (!playerStats.isEmpty()) {
                    data.put(playerId, playerStats);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void saveTask() {
        try {
            lock.lock();
            if (launch) {
                return;
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                try {
                    lock.lock();
                    saveFile();
                    launch = false;
                } finally {
                    lock.unlock();
                }
            }, 20L);
            launch = true;
        } finally {
            lock.unlock();
        }
    }

    public void saveFile() {
        try {
            lock.lock();
            config.save(file);
        } catch (IOException ignored) {
        } finally {
            lock.unlock();
        }
    }

    private String getPath(@NotNull UUID playerId, @NotNull String command) {
        return playerId + "." + command;
    }

    public void addCommand(@NotNull String command) {
        try {
            lock.lock();
            commands.add(command);
        } finally {
            lock.unlock();
        }
    }

    public void removeCommand(@NotNull String command) {
        try {
            lock.lock();
            commands.remove(command);
        } finally {
            lock.unlock();
        }
    }

    public int increment(@NotNull UUID playerId, @NotNull String command) {
        try {
            lock.lock();
            if (!commands.contains(command)) {
                return -1;
            }
            final Map<String, Integer> playerData = data.computeIfAbsent(playerId, k -> new HashMap<>());
            final int newStat = playerData.merge(command, 1, Integer::sum);
            config.set(getPath(playerId, command), newStat);
            return newStat;
        } finally {
            lock.unlock();
        }
    }

    public int get(@NotNull UUID playerId, @NotNull String command) {
        try {
            lock.lock();
            if (!commands.contains(command)) {
                return -1;
            }
            final Map<String, Integer> playerData = data.get(playerId);
            return playerData == null ? 0 : playerData.getOrDefault(command, 0);
        } finally {
            lock.unlock();
        }
    }

    public int getTotal(@NotNull UUID playerId) {
        try {
            lock.lock();
            final Map<String, Integer> playerData = data.get(playerId);
            return playerData == null ? 0 : playerData.values().stream().mapToInt(value -> value).sum();
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(@NotNull UUID playerId) {
        try {
            lock.lock();
            final boolean result = data.remove(playerId) != null;
            if (result) {
                config.set(playerId.toString(), null);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeStat(@NotNull UUID playerId, @NotNull String command) {
        try {
            lock.lock();
            if (!commands.contains(command)) {
                return false;
            }
            final Map<String, Integer> playerData = data.get(playerId);
            final boolean result = playerData != null && playerData.remove(command) != null;
            if (result) {
                config.set(getPath(playerId, command), null);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean allowStats(@NotNull String command) {
        try {
            lock.lock();
            return commands.contains(command);
        } finally {
            lock.unlock();
        }
    }

    public String[] getCommands() {
        try {
            lock.lock();
            return commands.toArray(new String[0]);
        } finally {
            lock.unlock();
        }
    }
}
