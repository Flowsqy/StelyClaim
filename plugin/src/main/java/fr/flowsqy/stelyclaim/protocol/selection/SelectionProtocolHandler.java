package fr.flowsqy.stelyclaim.protocol.selection;

import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.stelyclaim.api.action.ActionContext;

public interface SelectionProtocolHandler {

    void handle(@NotNull ActionContext context, @NotNull RegionManager regionManager,
            @NotNull ProtectedRegion selectedRegion);

}
