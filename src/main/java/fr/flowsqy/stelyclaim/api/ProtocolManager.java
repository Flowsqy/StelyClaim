package fr.flowsqy.stelyclaim.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProtocolManager {

    private final Map<String, ClaimHandler> handlers;

    public ProtocolManager() {
        this.handlers = new HashMap<>();
    }

    public void registerHandler(String id, ClaimHandler handler) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(handler);
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("A ClaimHandler is already register for the id '" + id + "'");
        }
        handlers.put(id, handler);
    }

    public boolean unregisterHandler(String id) {
        Objects.requireNonNull(id);
        return handlers.remove(id) != null;
    }

}
