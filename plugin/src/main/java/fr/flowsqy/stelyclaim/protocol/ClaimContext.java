package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ClaimContext {

    private World world;
    private final OwnerContext ownerContext;

    public ClaimContext(@NotNull ClaimHandler<?> claimHandler) {
        ownerContext = new OwnerContext(claimHandler);
    }

    public Optional<World> getWorld() {
        return Optional.of(world);
    }

    public void setWorld(@NotNull Supplier<World> worldSupplier, boolean force) {
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
