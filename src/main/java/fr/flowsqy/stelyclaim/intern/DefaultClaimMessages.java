package fr.flowsqy.stelyclaim.intern;

import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.CommandSender;

public class DefaultClaimMessages implements ClaimMessage {

    private final Messages messages;

    public DefaultClaimMessages(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean sendMessage(CommandSender sender, String path, String... replace) {
        return messages.sendMessage(sender, path, replace);
    }
}
