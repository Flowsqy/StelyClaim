package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class OwnerRetriever {

    public record Result<T extends ClaimOwner>(@NotNull ClaimHandler<T> handler,
                                               @NotNull Optional<T> owner) {

        public HandledOwner<T> toHandledOwner() {
            return new HandledOwner<>(handler, owner.orElseThrow());
        }

        public boolean isEmpty() {
            return owner.isEmpty();
        }

    }

    public static <T extends ClaimOwner> Result<T> retrieve(@NotNull Actor actor, @NotNull ClaimHandler<T> handler, @NotNull String arg) {
        return new Result<>(handler, Objects.requireNonNull(handler.getClaimCommandHandler()).getOwner(actor, arg));
    }

    public static <T extends ClaimOwner> Result<T> retrieve(@NotNull Actor actor, @NotNull ClaimHandler<T> handler, @NotNull Player arg) {
        return new Result<>(handler, Objects.requireNonNull(handler.getClaimCommandHandler()).getOwner(actor, arg));
    }

}
