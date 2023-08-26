package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public interface InteractProtocolHandler {

    void interactRegion(
            @NotNull RegionManager regionManager,
            @NotNull ProtectedRegion region,
            @NotNull ActionContext context
    );

}
