package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectionProtocol {

    public static final int SELECTION_NOT_DEFINED = ActionResult.registerResultCode();
    public static final int REGION_ALREADY_EXIST = ActionResult.registerResultCode();
    public static final int SELECTION_NOT_CUBOID = ActionResult.registerResultCode();

    public void process(@NotNull ActionContext<ClaimContextData> context, @NotNull SelectionProvider selectionProvider, @Nullable SelectionModifier selectionModifier, @NotNull SelectionProtocolHandler selectionProtocolHandler) {
        final Region selection = selectionProvider.getSelection(context);
        if (selection == null) {
            //messages.sendMessage(player, "claim.selection.empty");
            context.setResult(new ActionResult(SELECTION_NOT_DEFINED, false));
            return;
        }

        if (!context.getCustomData().own() && false /* TODO Check for modify ability */) {
            context.setResult(new ActionResult(InteractProtocol.CANT_OTHER, false));
            return;
        }

        final RegionManager regionManager = RegionManagerRetriever.retrieve(world.getName());
        if (regionManager == null) {
            context.setResult(new ActionResult(InteractProtocol.WORLD_NOT_HANDLED, false));
            return;
        }


        /*
        // TODO Move this in SelectionProtocolHandler implementation
        final ProtectedRegion currentRegion = regionManager.getRegion(regionName);
        //RegionNameManager.mustNotExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (currentRegion != null) {
            context.setResult(new ActionResult(REGION_ALREADY_EXIST, false));
            return;
        }*/

        if (selectionModifier != null) {
            selectionModifier.modify(selection, context);
        }

        if (!(selection instanceof CuboidRegion)) {
            // We could check for instanceof Polygonal2DRegion
            // But that currently does not match with the plugin pillar feature
            // Force cuboid
            //messages.sendMessage(player, "claim.selection.cuboid");
            context.setResult(new ActionResult(SELECTION_NOT_CUBOID, false));
            return;
        }

        final String regionName = handledOwner.getRegionName();
        final ProtectedRegion selectedRegion = new ProtectedCuboidRegion(regionName, selection.getMaximumPoint(), selection.getMinimumPoint());

        selectionProtocolHandler.handle(context, regionManager, selectedRegion);
    }

}
