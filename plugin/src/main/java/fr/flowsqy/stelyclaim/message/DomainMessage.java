package fr.flowsqy.stelyclaim.message;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.context.DomainContext;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DomainMessage {

    public void sendMessage(@NotNull ActionContext context, @NotNull FormattedMessages messages, String domain, String presence, String action) {
        final int code = context.getResult().orElseThrow().code();
        // Domain
        if (code == DomainProtocol.CANT_MODIFY) {
            final DomainContext domainContext = context.getCustomData(DomainContext.class);
            final boolean own = domainContext.getOwnerContext().isActorOwnTheClaim();
            final String ownerName = domainContext.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName();
            final String targetName = Objects.requireNonNull(domainContext.getTarget().orElseThrow().getName());
            messages.sendMessage(context.getActor().getBukkit(),
                    "claim.domain." + domain + "." + presence + (own ? "" : "-other"),
                    "%region%", "%target%", ownerName, targetName);
        }
        if (code == DomainProtocol.MODIFY) {
            final DomainContext domainContext = context.getCustomData(DomainContext.class);
            final boolean own = domainContext.getOwnerContext().isActorOwnTheClaim();
            final String ownerName = domainContext.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName();
            final String targetName = Objects.requireNonNull(domainContext.getTarget().orElseThrow().getName());
            messages.sendMessage(context.getActor().getBukkit(),
                    "claim.command." + action + domain + (own ? "" : "-other"),
                    "%region%", "%target%", ownerName, targetName);
        }
    }

}
