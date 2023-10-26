package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;

public interface ClaimOwner {

    /**
     * The name of the owner
     *
     * @return The name of the owner
     */
    @NotNull
    String getName();

    /**
     * Whether this {@link ClaimOwner} is considered owned by an actor
     *
     * @param actor The {@link Actor} to check
     * @return Whether this {@link ClaimOwner} is considered owned by the specified actor
     */
    boolean own(@NotNull Actor actor);

}
