package fr.flowsqy.stelyclaim.api;

public class ProtocolManager {

    /*
    private final SelectionProtocolOLD selectionProtocol;
    //private final MailManager mailManager;
    private final InteractProtocol interactProtocol;

    public ProtocolManager(@NotNull StelyClaimPlugin plugin) {
        selectionProtocol = new SelectionProtocolOLD(plugin);
        //mailManager = plugin.getMailManager();
        interactProtocol = new InteractProtocol(/*plugin*/ //);
    //}
/*
    public <T extends ClaimOwner> boolean define(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return selectionProtocol.process(world, actor, owner, SelectionProtocolOLD.Protocol.DEFINE);
    }

    public <T extends ClaimOwner> boolean redefine(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return selectionProtocol.process(world, actor, owner, SelectionProtocolOLD.Protocol.REDEFINE);
    }

    public void addMember(@NotNull ActionContext<ClaimContext> context, @NotNull OfflinePlayer target) {
        interact(context, new DomainProtocol(DomainProtocol.Protocol.ADD_MEMBER, target));
    }

    public void removeMember(@NotNull ActionContext<ClaimContext> context, @NotNull OfflinePlayer player) {
        interact(context, new DomainProtocol(DomainProtocol.Protocol.REMOVE_MEMBER, player));
    }

    public void addOwner(@NotNull ActionContext<ClaimContext> context, @NotNull OfflinePlayer player) {
        interact(context, new DomainProtocol(DomainProtocol.Protocol.ADD_OWNER, player));
    }

    public void removeOwner(@NotNull ActionContext<ClaimContext> context, @NotNull OfflinePlayer player) {
        interact(context, new DomainProtocol(DomainProtocol.Protocol.REMOVE_OWNER, player));
    }

    public void remove(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return interact(world, actor, owner, interactProtocol.getRemoveProtocolHandler());
    }

    public void interact(@NotNull ActionContext<ClaimContext> context, @NotNull InteractProtocolHandler interactHandler) {
        interactProtocol.process(context, interactHandler);
    }*/

}
