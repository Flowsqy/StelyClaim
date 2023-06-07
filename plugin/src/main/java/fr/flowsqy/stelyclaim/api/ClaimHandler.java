package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.Nullable;

public interface ClaimHandler<T extends ClaimOwner> {

    /**
     * A unique identifier that represent this type of handler
     *
     * @return The handler identifier
     */
    String getId();

    @Nullable ClaimInteractHandler<T> getClaimInteractHandler();

    /**
     * The owner of the claim
     *
     * @param claimIdentifier The {@link String} that identifies the owner of the claim
     * @return The {@link ClaimOwner} of the claim
     */
    T getOwner(String claimIdentifier);

    /**
     * Get the {@link String} that identifies the owner of the claim
     *
     * @param owner The {@link ClaimOwner} of the claim
     * @return The {@link String} that identifies the owner of the claim
     */
    String getIdentifier(T owner);

    /**
     * Get the {@link RegionModifier} for the Define operation
     *
     * @return The Define {@link RegionModifier}
     */
    RegionModifier<T> getDefineModifier();

    /**
     * Get the {@link RegionModifier} for the Redefine operation
     *
     * @return The Redefine {@link RegionModifier}
     */
    RegionModifier<T> getRedefineModifier();

    /**
     * The messages that is used by this type of handler
     *
     * @return The {@link FormattedMessages} used by this handler
     */
    FormattedMessages getMessages();

}
