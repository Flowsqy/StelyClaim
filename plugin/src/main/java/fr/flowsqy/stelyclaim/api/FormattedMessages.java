package fr.flowsqy.stelyclaim.api;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FormattedMessages extends CachedMessages {

    /**
     * Get formatted message ready to be sent
     *
     * @param path    The identifier of the message
     * @param replace A {@link String} array of the placeholders and their values
     * @return A formatted {@link String} message
     */
    @Nullable
    String getFormattedMessage(@NotNull String path, @NotNull String... replace);

    /**
     * Send a message
     * <p>
     * If the message stored is null, then nothing is sent
     *
     * @param sender  The receiver of the message
     * @param path    The identifier of the message
     * @param replace A {@link String} array of the placeholders and their values
     */
    void sendMessage(@NotNull CommandSender sender, @NotNull String path, @NotNull String... replace);

}
