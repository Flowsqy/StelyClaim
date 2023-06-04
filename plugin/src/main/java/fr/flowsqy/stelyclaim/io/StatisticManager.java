package fr.flowsqy.stelyclaim.io;

import fr.flowsqy.stelyclaim.command.claim.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StatisticManager {

    private final Plugin plugin;
    private final File file;
    private final YamlConfiguration config;
    private final Set<String> commands;
    private final Map<String, Map<String, Integer>> data;
    private final Lock lock;
    private boolean launch = false;

    public StatisticManager(Plugin plugin, File dataFolder) {
        this.plugin = plugin;
        this.file = new File(dataFolder, "statistics.yml");
        this.config = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        this.commands = new HashSet<>();
        this.data = new HashMap<>();
        lock = new ReentrantLock();
        initData();
    }

    public void initData() {
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection section))
                continue;
            final Map<String, Integer> stats = new HashMap<>();
            for (Map.Entry<String, Object> commandEntry : section.getValues(false).entrySet()) {
                final int value;
                try {
                    value = Integer.parseInt(commandEntry.getValue().toString());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                if (commands.contains(commandEntry.getKey())) {
                    stats.put(commandEntry.getKey(), value);
                }
            }
            if (!stats.isEmpty())
                data.put(entry.getKey(), stats);
        }
    }

    public void saveTask() {
        try {
            lock.lock();
            if (launch)
                return;
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

    private String getPath(String player, String command) {
        return player + "." + command;
    }

    public void initSubCommands(Iterable<SubCommand> subCommands) {
        commands.clear();
        for (SubCommand subCommand : subCommands) {
            if (subCommand.isStatistic())
                commands.add(subCommand.getName());
        }
    }

    public int add(CommandSender player, String command) {
        try {
            lock.lock();
            if (!commands.contains(command))
                return -1;
            final Map<String, Integer> playerData = data.computeIfAbsent(player.getName(), key -> new HashMap<>());
            final int stat = playerData.merge(command, 1, Integer::sum);
            config.set(getPath(player.getName(), command), stat);
            return stat;
        } finally {
            lock.unlock();
        }
    }

    public int get(String playerName, String command) {
        try {
            lock.lock();
            if (!commands.contains(command))
                return -1;
            final Map<String, Integer> playerData = data.get(playerName);
            return playerData == null ? 0 : playerData.getOrDefault(command, 0);
        } finally {
            lock.unlock();
        }
    }

    public int getTotal(String playerName) {
        try {
            lock.lock();
            final Map<String, Integer> playerData = data.get(playerName);
            return playerData == null ? 0 : playerData.values().stream().mapToInt(value -> value).sum();
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(String playerName) {
        try {
            lock.lock();
            final boolean result = data.remove(playerName) != null;
            if (result)
                config.set(playerName, null);
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeStat(String playerName, String command) {
        if (!commands.contains(command))
            return false;
        try {
            lock.lock();
            final Map<String, Integer> playerData = data.get(playerName);
            final boolean result = playerData != null && playerData.remove(command) != null;
            if (result)
                config.set(getPath(playerName, command), null);
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean allowStats(String command) {
        return commands.contains(command);
    }

    public Set<String> getCommands() {
        return commands;
    }
}
