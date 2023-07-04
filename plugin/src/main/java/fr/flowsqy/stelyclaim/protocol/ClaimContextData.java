package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClaimContextData {

    private final LazyOwner<?> lazyOwner;
    private String world;
    private boolean actorOwnTheClaim;

    public ClaimContextData(@NotNull ClaimHandler<?> claimHandler) {
        lazyOwner = new LazyOwner<>(claimHandler);
    }

    public Optional<String> getWorld() {
        return Optional.of(world);
    }

    public void setWorld(@NotNull String world) {
        this.world = world;
    }

    @NotNull
    public LazyOwner<?> getLazyOwner() {
        return lazyOwner;
    }

    public boolean isActorOwnTheClaim() {
        return actorOwnTheClaim;
    }

    public void setActorOwnTheClaim(boolean actorOwnTheClaim) {
        this.actorOwnTheClaim = actorOwnTheClaim;
    }

}
