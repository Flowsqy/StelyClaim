package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ClaimHandler<T extends ClaimOwner> {

    /**
     * A unique identifier that represent this type of handler
     *
     * @return The handler identifier
     */
    @NotNull
    String getId();

    @Nullable ClaimInteractHandler<T> getClaimInteractHandler();

    // TODO Throws an error if the owner can't be retrieved
    /**
     * The owner of the claim
     *
     * @param claimIdentifier The {@link String} that identifies the owner of the claim
     * @return The {@link ClaimOwner} of the claim
     */
    @NotNull
    HandledOwner<T> getOwner(@NotNull String claimIdentifier);

    /**
     * Get the {@link String} that identifies the owner of the claim
     *
     * @param owner The {@link ClaimOwner} of the claim
     * @return The {@link String} that identifies the owner of the claim
     */
    @NotNull
    String getIdentifier(@NotNull T owner);

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

}
