package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.protocol.SelectionProtocol;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ProtocolManager {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+$");

    private final Map<String, ClaimHandler<?>> handlers;
    private final SelectionProtocol selectionProtocol;

    public ProtocolManager(StelyClaimPlugin plugin) {
        this.handlers = new HashMap<>();

        this.selectionProtocol = new SelectionProtocol(plugin);
    }

    public void registerHandler(ClaimHandler<?> handler) {
        Objects.requireNonNull(handler);
        final String id = handler.getId();
        Objects.requireNonNull(id);
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("Invalid id, must match pattern " + ID_PATTERN.pattern());
        }
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("A ClaimHandler is already register for the id '" + id + "'");
        }
        handlers.put(id, handler);
    }

    public boolean unregisterHandler(ClaimHandler<?> handler) {
        Objects.requireNonNull(handler);
        return handlers.remove(handler.getId()) != null;
    }

    public <T extends ClaimOwner> boolean define(Player sender, ClaimHandler<T> handler, T owner) {
        return selectionProtocol.process(sender, handler, owner, Protocol.DEFINE);
    }

    public <T extends ClaimOwner> boolean redefine(Player sender, ClaimHandler<T> handler, T owner) {
        return selectionProtocol.process(sender, handler, owner, Protocol.REDEFINE);
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
