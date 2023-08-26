package fr.flowsqy.stelyclaim.pillar;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PillarManager {

    private final Map<UUID, PillarData> sessionMap;

    public PillarManager() {
        sessionMap = new HashMap<>();
    }

    public void load(@NotNull Plugin plugin) {
        final DisconnectListener disconnectListener = new DisconnectListener(this);
        disconnectListener.load(plugin);
    }

    @NotNull
    public PillarData getOrCreateSession(@NotNull UUID sessionId) {
        return sessionMap.computeIfAbsent(sessionId, k -> new PillarData());
    }

    @Nullable
    public PillarData getSession(@NotNull UUID sessionId) {
        return sessionMap.get(sessionId);
    }

    public void removeSession(@NotNull UUID sessionId) {
        sessionMap.remove(sessionId);
    }
}
