package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectionProtocolHandler {

    void handle(@NotNull ActionContext<ClaimContextData> context, @NotNull RegionManager regionManager, @Nullable ProtectedRegion currentRegion, @NotNull ProtectedRegion selectedRegion);

}
