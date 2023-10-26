package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CachedMessages {

    /**
     * Get a message
     *
     * @param identifier The {@link String} identifier of the message
     * @return The {@link String} message stored. {@code null} if it does not exist
     */
    @Nullable
    String getMessage(@NotNull String identifier);

}
