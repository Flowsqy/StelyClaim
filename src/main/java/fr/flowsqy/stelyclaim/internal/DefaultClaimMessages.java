package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.io.Messages;

public class DefaultClaimMessages implements ClaimMessage {

    private final Messages messages;

    public DefaultClaimMessages(Messages messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage(String path, String... replace) {
        return messages.getMessage(path, replace);
    }

}
