package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ClaimContext {

    private String world;
    private final OwnerContext ownerContext;

    public ClaimContext(@NotNull ClaimHandler<?> claimHandler) {
        ownerContext = new OwnerContext(claimHandler);
    }

    public Optional<String> getWorld() {
        return Optional.of(world);
    }

    public void setWorld(@NotNull Supplier<String> worldSupplier, boolean force) {
        if (this.world != null && !force) {
            return;
        }
        this.world = worldSupplier.get();
    }

    @NotNull
    public OwnerContext getOwnerContext() {
        return ownerContext;
    }

}
