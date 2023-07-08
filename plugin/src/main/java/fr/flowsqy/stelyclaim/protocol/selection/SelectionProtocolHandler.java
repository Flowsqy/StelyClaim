package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public interface SelectionProtocolHandler {

    void handle(@NotNull ActionContext<ClaimContext> context, @NotNull RegionManager regionManager, @NotNull ProtectedRegion selectedRegion);

}
