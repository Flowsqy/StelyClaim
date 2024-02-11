package fr.flowsqy.stelyclaim.message;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FallbackFormattedMessages implements FormattedMessages {

    private final FormattedMessages fallback, preferred;

    public FallbackFormattedMessages(@NotNull FormattedMessages fallback, @NotNull FormattedMessages preferred) {
        this.fallback = fallback;
        this.preferred = preferred;
    }

    @Override
    public @Nullable String getMessage(@NotNull String identifier) {
        final String message = preferred.getMessage(identifier);
        return message == null ? fallback.getMessage(identifier) : message;
    }

    @Override
    public @Nullable String getFormattedMessage(@NotNull String path, @NotNull String... replace) {
        final String message = preferred.getFormattedMessage(path, replace);
        return message == null ? fallback.getFormattedMessage(path, replace) : message;
    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull String path, @NotNull String... replace) {
        final String message = getFormattedMessage(path, replace);
        if (message == null) {
            return;
        }
        final BaseComponent[] components = TextComponent.fromLegacyText(message);
        sender.spigot().sendMessage(components);
    }
}
