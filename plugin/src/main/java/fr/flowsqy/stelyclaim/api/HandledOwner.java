package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import org.jetbrains.annotations.NotNull;

public record HandledOwner<T extends ClaimOwner>(@NotNull ClaimHandler<T> handler, @NotNull T owner) {

    @NotNull
    public String getRegionName() {
        return RegionNameManager.getRegionName(this);
    }

}
