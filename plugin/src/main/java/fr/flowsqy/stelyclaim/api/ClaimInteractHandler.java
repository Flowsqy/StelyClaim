package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ClaimInteractHandler<T extends ClaimOwner> {

    /**
     * Retrieve an owner from a command arg
     *
     * @param commandArg The command argument
     * @return An {@link Optional} owner
     */
    @NotNull
    Optional<T> getOwner(@NotNull Actor actor, @NotNull String commandArg);

    /**
     * Retrieve an owner from a command sender
     *
     * @param player The command sender
     * @return An {@link Optional} owner
     */
    @NotNull
    Optional<T> getOwner(@NotNull Actor actor, @NotNull Player player);

    @NotNull
    FormattedMessages getMessages();

}
