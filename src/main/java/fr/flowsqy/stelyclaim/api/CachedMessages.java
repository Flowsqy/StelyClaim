package fr.flowsqy.stelyclaim.api;

public interface CachedMessages {

    /**
     * Get a message
     *
     * @param identifier The {@link String} identifier of the message
     * @return The {@link String} message stored. {@code null} if it does not exist
     */
    String getMessage(String identifier);

}
