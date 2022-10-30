package fr.flowsqy.stelyclaim.common;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class SimpleFormattedMessages implements FormattedMessages {

    private final Map<String, String> defaultPlaceholders;

    public SimpleFormattedMessages(Map<String, String> defaultPlaceholders) {
        this.defaultPlaceholders = defaultPlaceholders;
    }

    @Override
    public String getFormattedMessage(String path, String... replace) {
        String message = getMessage(path);
        if (message == null) {
            return null;
        }

        message = applyDefaultPlaceholders(message);

        if (replace != null) {
            final int middle = (replace.length - replace.length % 2) / 2;
            for (int index = 0; index < middle; index++) {
                message = message.replace(replace[index], replace[index + middle]);
            }
        }

        return message;
    }

    /**
     * Apply the default placeholders
     *
     * @param message The message that need to be formatted
     * @return The formatted message
     */
    private String applyDefaultPlaceholders(String message) {
        for (Map.Entry<String, String> entry : defaultPlaceholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        return message;
    }

    @Override
    public boolean sendMessage(CommandSender sender, String path, String... replace) {
        if (sender == null) {
            return true;
        }
        final String message = getFormattedMessage(path, replace);
        if (message != null) {
            sender.sendMessage(message);
        }
        return true;
    }
}
