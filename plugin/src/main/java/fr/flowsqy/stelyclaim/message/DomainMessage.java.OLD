package fr.flowsqy.stelyclaim.message;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.context.DomainContext;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DomainMessage {

    private final static String MEMBER_DOMAIN = "member";
    private final static String OWNER_DOMAIN = "owner";
    private final static String CANT_ADD = "already";
    private final static String CANT_REMOVE = "not";
    private final static String ADD = "add";
    private final static String REMOVE = "remove";
    private final static String OTHER_SUFFIX = "-other";

    private final FormattedMessages messages;
    private final String pathPrefix;

    public DomainMessage(@NotNull FormattedMessages messages, @NotNull String pathPrefix) {
        this.messages = messages;
        this.pathPrefix = pathPrefix;
    }

    public void sendMessage(@NotNull ActionContext context, @NotNull DomainProtocol.Protocol protocol) {
        final int code = context.getResult().orElseThrow().code();
        if (code == DomainProtocol.CANT_MODIFY) {
            sendMessage(context, protocol, CANT_ADD, CANT_REMOVE);
        }
        if (code == DomainProtocol.MODIFY) {
            sendMessage(context, protocol, ADD, REMOVE);
        }
    }

    private void sendMessage(@NotNull ActionContext context, @NotNull DomainProtocol.Protocol protocol, @NotNull String addPath, @NotNull String removePath) {
        final DomainContext domainContext = context.getCustomData(DomainContext.class);
        final String domain = protocol.isOwner() ? OWNER_DOMAIN : MEMBER_DOMAIN;
        final boolean own = domainContext.getOwnerContext().isActorOwnTheClaim();
        final String other = own ? OTHER_SUFFIX : "";
        final String path = protocol.isAdd() ? addPath : removePath;
        final String ownerName = domainContext.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName();
        final String targetName = Objects.requireNonNull(domainContext.getTarget().orElseThrow().getName());

        messages.sendMessage(context.getActor().getBukkit(),
                pathPrefix + "." + domain + "." + path + other,
                "%region%", "%target%", ownerName, targetName);
    }

}
