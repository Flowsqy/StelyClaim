package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.LazyHandledOwner;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OwnerContext {

    private LazyHandledOwner<?> lazyHandledOwner;
    private boolean actorOwnTheRegion;
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

    public boolean isActorOwnTheRegion() {
        if (!ownInit) {
            throw new IllegalStateException();
        }
        return actorOwnTheRegion;
    }

    public void setActorOwnTheRegion(Supplier<Boolean> ownProvider, boolean force) {
        if (ownInit && !force) {
            return;
        }
        this.actorOwnTheRegion = ownProvider.get();
        ownInit = false;
    }

}
