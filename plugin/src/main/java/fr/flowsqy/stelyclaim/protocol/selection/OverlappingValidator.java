package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import org.jetbrains.annotations.NotNull;

public class OverlappingValidator implements RegionValidator {

    public static final int REGION_OVERLAP;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            REGION_OVERLAP = register.get();
        } finally {
            register.unlock();
        }
    }

    private final OverlappingRegion overlappingRegion;

    public OverlappingValidator(@NotNull OverlappingRegion overlappingRegion) {
        this.overlappingRegion = overlappingRegion;
    }

    @Override
    public boolean validate(@NotNull ActionContext context, @NotNull RegionManager regionManager, @NotNull ProtectedRegion selectedRegion) {
        overlappingRegion.process(regionManager, selectedRegion);
        if (overlappingRegion.getRegionsOverlapping().length == 0) {
            return true;
        }
        context.setResult(new ActionResult(REGION_OVERLAP, false));
        return false;
    }
}
