package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LazyHandledOwner<T extends ClaimOwner> {

    private final ClaimHandler<T> handler;
    private T owner;

    public LazyHandledOwner(@NotNull ClaimHandler<T> handler) {
        this(handler, null);
    }

    public LazyHandledOwner(@NotNull ClaimHandler<T> handler, @Nullable T owner) {
        this.handler = handler;
        this.owner = owner;
    }

    @NotNull
    public ClaimHandler<T> getHandler() {
        return handler;
    }

    @Nullable
    public T getOwner() {
        return owner;
    }

    public void setOwner(@Nullable T owner) {
        this.owner = owner;
    }

    @NotNull
    public HandledOwner<T> toHandledOwner() {
        return new HandledOwner<>(handler, Objects.requireNonNull(owner));
    }

    public void retrieve(@NotNull Actor actor, @NotNull String arg) {
        Objects.requireNonNull(handler.getClaimInteractHandler()).getOwner(actor, arg).ifPresent(this::setOwner);
    }

    public void retrieve(@NotNull Actor actor, @NotNull Player player) {
        Objects.requireNonNull(handler.getClaimInteractHandler()).getOwner(actor, player).ifPresent(this::setOwner);
    }

}
