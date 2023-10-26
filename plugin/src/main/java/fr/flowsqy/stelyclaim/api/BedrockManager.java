package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.io.IdDatabase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BedrockManager {

    private final File file;
    private final Set<UUID> players;

    public BedrockManager(@NotNull File dataFolder) {
        this.file = new File(dataFolder, "bedrock.db");
        this.players = new HashSet<>();
    }

    public void load() {
        players.clear();
        players.addAll(new IdDatabase().load(file));
    }

    public void save() {
        new IdDatabase().save(file, players);
    }

    public boolean toggle(@NotNull UUID playerId) {
        if (!players.remove(playerId))
            return players.add(playerId);
        return false;
    }

    public boolean has(@NotNull UUID playerId) {
        return players.contains(playerId);
    }

}
