package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedefineHandler implements SelectionProtocolHandler {

    public static final int REGION_NOT_EXIST;
    public static final int REDEFINE;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            REGION_NOT_EXIST = register.get();
            REDEFINE = register.get();
        } finally {
            register.unlock();
        }
    }

    private final RegionModifier regionModifier;

    public RedefineHandler(@Nullable RegionModifier regionModifier) {
        this.regionModifier = regionModifier;
    }

    @Override
    public void handle(@NotNull ActionContext context, @NotNull RegionManager regionManager, @NotNull ProtectedRegion selectedRegion) {
        final ProtectedRegion currentRegion = regionManager.getRegion(selectedRegion.getId());
        //RegionNameManager.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (currentRegion == null) {
            context.setResult(new ActionResult(REGION_NOT_EXIST, false));
            return;
        }

        selectedRegion.copyFrom(currentRegion);

        if (regionModifier != null) {
            regionModifier.modify(context, regionManager, selectedRegion);
        }

        regionManager.addRegion(selectedRegion);
        context.setResult(new ActionResult(REDEFINE, true));
    }
}
