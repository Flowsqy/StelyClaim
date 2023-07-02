package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.HandlerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegionHandler {

    private final String regionName;
    private byte correctId;
    private String[] parts;

    public RegionHandler(@NotNull String regionName) {
        this.regionName = regionName;
        correctId = 0b0;
    }

    @NotNull
    public String getRegionName() {
        return regionName;
    }

    /**
     * Whether this region is a 'StelyClaim' region
     *
     * @return {@code true} if it's a 'StelyClaim' region. {@code false} otherwise
     */
    public boolean isInternalRegion() {
        if ((correctId & 0b1) == 0b1) {
            return (correctId & 0b10) == 0b10;
        }
        correctId |= 0b1;
        if (!RegionNameManager.isCorrectId(regionName)) {
            return false;
        }
        correctId |= 0b10;
        return true;
    }

    @NotNull
    private String[] getParts() {
        if (parts == null) {
            parts = RegionNameManager.getParts(regionName);
        }
        return parts;
    }

    @Nullable
    public ClaimHandler<?> getHandler(@NotNull HandlerRegistry registry) {
        if (!isInternalRegion()) {
            throw new IllegalArgumentException("This is not an internal region");
        }
        final String handlerName = getParts()[0];
        return registry.getHandler(handlerName);
    }

    @Nullable
    public HandledOwner<?> getOwner(@NotNull HandlerRegistry registry) {
        final ClaimHandler<?> handler = getHandler(registry);
        if (handler == null) {
            return null;
        }
        try {
            return handler.getOwner(getParts()[1]);
        } catch (Exception ignored) {
            return null;
        }
    }

    @NotNull
    public String getName(@NotNull HandlerRegistry registry) {
        if (!isInternalRegion()) {
            return regionName;
        }
        final HandledOwner<?> owner = getOwner(registry);
        if (owner == null) {
            return regionName;
        }
        return owner.owner().getName();
    }

}
