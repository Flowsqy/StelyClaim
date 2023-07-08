package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.LazyHandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OwnerContext {

    private LazyHandledOwner<?> lazyHandledOwner;
    private boolean actorOwnTheClaim;
    private boolean ownInit;

    public OwnerContext(@NotNull ClaimHandler<?> handler) {
        setHandler(handler);
    }

    @NotNull
    public ClaimHandler<?> getHandler() {
        return lazyHandledOwner.getHandler();
    }

    public void setHandler(@NotNull ClaimHandler<?> handler) {
        lazyHandledOwner = new LazyHandledOwner<>(handler);
    }

    @NotNull
    public LazyHandledOwner<?> getLazyHandledOwner() {
        return lazyHandledOwner;
    }

    public boolean isActorOwnTheClaim() {
        if (!ownInit) {
            throw new IllegalStateException();
        }
        return actorOwnTheClaim;
    }

    public void setActorOwnTheClaim(@NotNull Supplier<Boolean> ownProvider, boolean force) {
        if (ownInit && !force) {
            return;
        }
        this.actorOwnTheClaim = ownProvider.get();
        ownInit = false;
    }

    public void calculateOwningProperty(@NotNull Actor actor, boolean force) {
        setActorOwnTheClaim(() -> lazyHandledOwner.toHandledOwner().owner().own(actor), force);
    }

}
