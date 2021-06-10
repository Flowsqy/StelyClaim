package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.CommandSender;

public class DefaultClaimMessages implements ClaimMessage {

    private final Messages messages;

    public DefaultClaimMessages(Messages messages) {
        this.messages = messages;
    }

    @Override
    public void sendMessage(CommandSender sender, String path, String... replace) {
        if (sender == null)
            return;
        messages.sendMessage(sender, path, replace);
    }
}
