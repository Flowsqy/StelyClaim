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

public class ProtocolManager {

    private final SelectionProtocol selectionProtocol;
    private final MailManager mailManager;
    private final InteractProtocol interactProtocol;

    public ProtocolManager(@NotNull StelyClaimPlugin plugin) {
        selectionProtocol = new SelectionProtocol(plugin);
        mailManager = plugin.getMailManager();
        interactProtocol = new InteractProtocol(plugin);
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
