package fr.flowsqy.stelyclaim.protocol.context;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.command.claim.HandlerContext;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class InteractContext implements HandlerContext {

    private final OwnerContext ownerContext;
    private World world;

    public InteractContext(@NotNull ClaimHandler<?> claimHandler) {
        ownerContext = new OwnerContext(claimHandler);
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
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

    @Override
    public @NotNull ClaimHandler<?> getHandler() {
        return ownerContext.getHandler();
    }

}
