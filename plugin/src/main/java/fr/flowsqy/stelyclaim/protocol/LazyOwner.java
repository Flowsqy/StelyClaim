package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LazyOwner<T extends ClaimOwner> {

    private String identifier;
    private ClaimHandler<T> handler;
    private boolean dirty;
    private HandledOwner<T> handledOwner;

    public LazyOwner(@NotNull ClaimHandler<T> handler) {
        this.handler = handler;
        dirty = true;
    }

    public Optional<String> getIdentifier() {
        return Optional.ofNullable(identifier);
    }

    public void setIdentifier(@NotNull String identifier) {
        this.identifier = identifier;
        dirty = true;
    }

    @NotNull
    public ClaimHandler<T> getHandler() {
        return handler;
    }

    public void setHandler(@NotNull ClaimHandler<T> handler) {
        this.handler = handler;
        dirty = true;
    }

    @NotNull
    public HandledOwner<T> getHandledOwner() {
        if (dirty) {
            handledOwner = handler.getOwner(getIdentifier().orElseThrow());
            dirty = false;
        }
        return handledOwner;
    }

}
