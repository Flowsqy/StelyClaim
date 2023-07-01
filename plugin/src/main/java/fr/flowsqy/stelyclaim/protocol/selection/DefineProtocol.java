package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.jetbrains.annotations.NotNull;

public class DefineProtocol {

    public static final int SELECTION_NOT_DEFINED = ActionResult.registerResultCode();
    public static final int REGION_ALREADY_EXIST = ActionResult.registerResultCode();

    public void process(@NotNull ActionContext<ClaimContextData> context, @NotNull SelectionProvider selectionProvider) {
        final Region selection = selectionProvider.getSelection(context);
        if(selection == null) {
            //messages.sendMessage(player, "claim.selection.empty");
            context.setResult(new ActionResult(SELECTION_NOT_DEFINED, false));
            return;
        }

        /*
        if (!(selection instanceof CuboidRegion)) {
            messages.sendMessage(player, "claim.selection.cuboid");
            return false;
        }*/

        if(!context.getCustomData().own() && false /* TODO Check for modify ability */) {
            context.setResult(new ActionResult(InteractProtocol.CANT_OTHER, false));
            return;
        }

        final RegionManager regionManager = RegionManagerRetriever.retrieve(world.getName());
        if (regionManager == null) {
            context.setResult(new ActionResult(InteractProtocol.WORLD_NOT_HANDLED, false));
            return;
        }

        final String regionName = RegionNameManager.getRegionName(handledOwner);

        final ProtectedRegion region = regionManager.getRegion(regionName);
        //RegionNameManager.mustNotExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (region != null) {
            context.setResult(new ActionResult(REGION_ALREADY_EXIST, false));
            return;
        }


    }

}
