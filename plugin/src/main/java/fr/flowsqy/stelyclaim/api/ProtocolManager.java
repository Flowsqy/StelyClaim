package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import fr.flowsqy.stelyclaim.protocol.selection.SelectionProtocol;
import fr.flowsqy.stelyclaim.util.MailManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProtocolManager {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+$");

    private final Map<String, ClaimHandler<?>> handlers;
    private final SelectionProtocol selectionProtocol;
    private final MailManager mailManager;
    private final InteractProtocol interactProtocol;

    public ProtocolManager(StelyClaimPlugin plugin) {
        this.handlers = new HashMap<>();

        this.selectionProtocol = new SelectionProtocol(plugin);
        mailManager = plugin.getMailManager();
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

    @SuppressWarnings("unused") //API
    public boolean unregisterHandler(ClaimHandler<?> handler) {
        Objects.requireNonNull(handler);
        return handlers.remove(handler.getId()) != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends ClaimOwner, S extends ClaimHandler<T>> S getHandler(String id) {
        return id == null ? null : (S) handlers.get(id);
    }

    public <T extends ClaimOwner> boolean define(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return selectionProtocol.process(world, actor, owner, SelectionProtocol.Protocol.DEFINE);
    }

    public <T extends ClaimOwner> boolean redefine(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return selectionProtocol.process(world, actor, owner, SelectionProtocol.Protocol.REDEFINE);
    }

    public <T extends ClaimOwner> boolean addMember(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer target) {
        return interact(world, actor, owner, new DomainProtocol(DomainProtocol.Protocol.ADDMEMBER, mailManager, target));
    }

    public <T extends ClaimOwner> boolean removeMember(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer player) {
        return interact(world, actor, owner, new DomainProtocol(DomainProtocol.Protocol.REMOVEMEMBER, mailManager, player));
    }

    public <T extends ClaimOwner> boolean addOwner(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer player) {
        return interact(world, actor, owner, new DomainProtocol(DomainProtocol.Protocol.ADDOWNER, mailManager, player));
    }

    public <T extends ClaimOwner> boolean removeOwner(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer player) {
        return interact(world, actor, owner, new DomainProtocol(DomainProtocol.Protocol.REMOVEOWNER, mailManager, player));
    }

    public <T extends ClaimOwner> boolean remove(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return interact(world, actor, owner, interactProtocol.getRemoveProtocolHandler());
    }

    public <T extends ClaimOwner> boolean interact(@NotNull World world, @NotNull Actor sender, @NotNull HandledOwner<T> owner, @NotNull InteractProtocolHandler interactHandler) {
        return interactProtocol.process(world, sender, owner, interactHandler);
    }

}
