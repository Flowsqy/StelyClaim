package fr.flowsqy.stelyclaim.api;

import org.bukkit.command.CommandSender;

public interface ClaimMessage {

    String getMessage(String path, String... replace);

    default boolean sendMessage(CommandSender sender, String path, String... replace) {
        if (sender == null)
            return true;
        final String message = getMessage(path, replace);
        if (message != null)
            sender.sendMessage(message);
        return true;
    }

}
