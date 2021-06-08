package fr.flowsqy.stelyclaim.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

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

    public boolean define(Player sender, ClaimOwner owner) {
        return false;
    }

    public boolean redefine(Player sender, ClaimOwner owner) {
        return false;
    }

    public boolean addMember(World world, ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean removeMember(World world, ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean addOwner(World world, ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean removeOwner(World world, ClaimOwner owner, UUID player) {
        return false;
    }

    public boolean remove(World world, ClaimOwner owner) {
        return false;
    }

}
