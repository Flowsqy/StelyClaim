package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import fr.flowsqy.stelyclaim.protocol.selection.SelectionProtocol;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProtocolManager {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+$");

    private final Map<String, ClaimHandler<?>> handlers;
    private final SelectionProtocol selectionProtocol;
    private final InteractProtocol interactProtocol;

    public ProtocolManager(StelyClaimPlugin plugin) {
        this.handlers = new HashMap<>();

        this.selectionProtocol = new SelectionProtocol(plugin);
        interactProtocol = new InteractProtocol(plugin);
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

    @SuppressWarnings("unchecked")
    public <T extends ClaimOwner, S extends ClaimHandler<T>> S getHandler(String id) {
        return id == null ? null : (S) handlers.get(id);
    }

    public <T extends ClaimOwner> boolean define(Player sender, ClaimHandler<T> handler, T owner) {
        return selectionProtocol.process(sender, handler, owner, SelectionProtocol.Protocol.DEFINE);
    }

    public <T extends ClaimOwner> boolean redefine(Player sender, ClaimHandler<T> handler, T owner) {
        return selectionProtocol.process(sender, handler, owner, SelectionProtocol.Protocol.REDEFINE);
    }

    public <T extends ClaimOwner> boolean addMember(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer player) {
        return DomainProtocol.process(world, sender, handler, owner, player, DomainProtocol.Protocol.ADDMEMBER);
    }

    public <T extends ClaimOwner> boolean removeMember(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer player) {
        return DomainProtocol.process(world, sender, handler, owner, player, DomainProtocol.Protocol.REMOVEMEMBER);
    }

    public <T extends ClaimOwner> boolean addOwner(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer player) {
        return DomainProtocol.process(world, sender, handler, owner, player, DomainProtocol.Protocol.ADDOWNER);
    }

    public <T extends ClaimOwner> boolean removeOwner(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer player) {
        return DomainProtocol.process(world, sender, handler, owner, player, DomainProtocol.Protocol.REMOVEOWNER);
    }

    public <T extends ClaimOwner> boolean remove(World world, Player sender, ClaimHandler<T> handler, T owner) {
        return interact(world, sender, handler, owner, interactProtocol.getRemoveProtocolHandler());
    }

    public <T extends ClaimOwner> boolean interact(World world, Player sender, ClaimHandler<T> handler, T owner, InteractProtocolHandler interactHandler) {
        return interactProtocol.process(world, sender, handler, owner, interactHandler);
    }

}
