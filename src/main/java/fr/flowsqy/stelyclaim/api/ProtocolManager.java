package fr.flowsqy.stelyclaim.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProtocolManager {

    private final Map<String, ClaimHandler<?>> handlers;

    public ProtocolManager() {
        this.handlers = new HashMap<>();
    }

    public void registerHandler(String id, ClaimHandler<?> handler) {
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

    public boolean define(ClaimOwner owner) {
        return false;
    }

    public boolean redefine(ClaimOwner owner) {
        return false;
    }

    public boolean addMember(ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean removeMember(ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean addOwner(ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean removeOwner(ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean remove(ClaimOwner owner) {
        return false;
    }

}
