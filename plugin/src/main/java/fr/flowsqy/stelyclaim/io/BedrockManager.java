package fr.flowsqy.stelyclaim.io;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BedrockManager {

    private final File file;
    private final YamlConfiguration config;
    private final Set<String> players;

    public BedrockManager(File dataFolder) {
        this.file = new File(dataFolder, "bedrock.yml");
        this.config = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        this.players = new HashSet<>(config.getStringList("players"));
    }

    public void save() {
        config.set("players", new ArrayList<>(players));
        try {
            config.save(file);
        } catch (IOException ignored) {
        }
    }

    public boolean toggle(String player, boolean save) {
        final boolean result = toggle(player);
        if (save)
            save();
        return result;
    }

    public boolean toggle(String player) {
        if (!players.remove(player))
            return players.add(player);
        return false;
    }

    public boolean has(String player) {
        return players.contains(player);
    }

}
