package fr.flowsqy.stelyclaim.io;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BedrockManager {

    private final File file;
    private final YamlConfiguration config;
    private final Set<String> players;

    public BedrockManager(File dataFolder){
        this.file = new File(dataFolder, "bedrock.yml");
        this.config = new YamlConfiguration();
        this.players = new HashSet<>();
    }

    public void save() {
        config.set("players", players);
        try {
            config.save(file);
        } catch (IOException ignored) {}
    }

    public boolean toggle(String player, boolean save){
        final boolean result = toggle(player);
        if(save)
            save();
        return result;
    }

    public boolean toggle(String player){
        if(!players.remove(player))
            return players.add(player);
        return false;
    }


}
