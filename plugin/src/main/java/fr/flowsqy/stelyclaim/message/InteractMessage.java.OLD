package fr.flowsqy.stelyclaim.message;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.context.InteractContext;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.jetbrains.annotations.NotNull;

public class InteractMessage {

    private final FormattedMessages messages;

    public InteractMessage(@NotNull FormattedMessages messages) {
        this.messages = messages;
    }

    public void sendMessage(@NotNull ActionContext context) {
        final int code = context.getResult().orElseThrow().code();
        if (code == InteractProtocol.WORLD_NOT_HANDLED) {
            messages.sendMessage(context.getActor().getBukkit(), "claim.world.nothandle");
            return;
        }
        if (code == InteractProtocol.REGION_NOT_EXIST) {
            // TODO Change message path to better integrate with new per-handler messages
            final InteractContext interactContext = context.getCustomData(InteractContext.class);
            final boolean own = interactContext.getOwnerContext().isActorOwnTheClaim();
            final String ownerName = interactContext.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName();
            messages.sendMessage(context.getActor().getBukkit(),
                    "claim.exist.not" + (own ? "" : "-other"),
                    "%region%", ownerName);
            return;
        }
        if (code == InteractProtocol.TRY_INTERACT_GLOBAL) {
            messages.sendMessage(context.getActor().getBukkit(), "claim.interactglobal");
        }
    }

}
