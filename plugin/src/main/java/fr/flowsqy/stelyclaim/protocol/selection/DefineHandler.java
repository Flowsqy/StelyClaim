package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefineHandler implements SelectionProtocolHandler {

    public static final int DEFINE = ActionResult.registerResultCode();

    private final RegionModifier regionModifier;

    public DefineHandler(@Nullable RegionModifier regionModifier) {
        this.regionModifier = regionModifier;
    }

    @Override
    public void handle(@NotNull ActionContext<ClaimContextData> context, @NotNull RegionManager regionManager, @NotNull ProtectedRegion selectedRegion) {
        if (regionModifier != null) {
            regionModifier.modify(context, regionManager, selectedRegion);
        }
        regionManager.addRegion(selectedRegion);
        context.setResult(new ActionResult(DEFINE, true));
    }

}
