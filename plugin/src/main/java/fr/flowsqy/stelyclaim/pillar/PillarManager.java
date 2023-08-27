package fr.flowsqy.stelyclaim.pillar;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PillarManager {

    private final Map<UUID, PillarSession> sessionMap;

    public PillarManager() {
        sessionMap = new HashMap<>();
    }

    public void load(@NotNull Plugin plugin) {
        final RemoveSessionListener removeSessionListener = new RemoveSessionListener(this);
        removeSessionListener.load(plugin);
    }

    @NotNull
    public PillarSession getOrCreateSession(@NotNull UUID sessionId) {
        return sessionMap.computeIfAbsent(sessionId, k -> new PillarSession());
    }

    @Nullable
    public PillarSession getSession(@NotNull UUID sessionId) {
        return sessionMap.get(sessionId);
    }

    public void removeSession(@NotNull UUID sessionId) {
        sessionMap.remove(sessionId);
    }
}
