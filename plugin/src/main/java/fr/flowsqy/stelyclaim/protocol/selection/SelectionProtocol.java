package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.protocol.OwnerContext;
import fr.flowsqy.stelyclaim.protocol.ProtocolInteractChecker;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectionProtocol {

    public static final int SELECTION_NOT_DEFINED = ActionResult.registerResultCode();
    public static final int SELECTION_NOT_CUBOID = ActionResult.registerResultCode();

    private final @NotNull SelectionProvider selectionProvider;
    private final @Nullable SelectionModifier selectionModifier;
    private final @Nullable RegionValidator regionValidator;
    private final @NotNull SelectionProtocolHandler selectionProtocolHandler;
    private final @NotNull ProtocolInteractChecker protocolInteractChecker;

    public SelectionProtocol(@NotNull SelectionProvider selectionProvider, @Nullable SelectionModifier selectionModifier, @Nullable RegionValidator regionValidator, @NotNull SelectionProtocolHandler selectionProtocolHandler, @NotNull ProtocolInteractChecker protocolInteractChecker) {
        this.selectionProvider = selectionProvider;
        this.selectionModifier = selectionModifier;
        this.regionValidator = regionValidator;
        this.selectionProtocolHandler = selectionProtocolHandler;
        this.protocolInteractChecker = protocolInteractChecker;
    }

    public void process(@NotNull ActionContext<ClaimContext> context) {
        final Region selection = selectionProvider.getSelection(context);
        if (selection == null) {
            //messages.sendMessage(player, "claim.selection.empty");
            context.setResult(new ActionResult(SELECTION_NOT_DEFINED, false));
            return;
        }

        final ClaimContext claimContext = context.getCustomData().orElseThrow();
        final OwnerContext ownerContext = claimContext.getOwnerContext();
        ownerContext.calculateOwningProperty(context.getActor(), false);
        if (!claimContext.getOwnerContext().isActorOwnTheClaim() && !protocolInteractChecker.canInteractNotOwned(context)) {
            context.setResult(new ActionResult(InteractProtocol.CANT_OTHER, false));
            return;
        }

        final RegionManager regionManager = RegionManagerRetriever.retrieve(claimContext.getWorld().orElseThrow().getName());
        if (regionManager == null) {
            context.setResult(new ActionResult(InteractProtocol.WORLD_NOT_HANDLED, false));
            return;
        }

        if (selectionModifier != null) {
            selectionModifier.modify(selection, context);
        }

        if (!(selection instanceof CuboidRegion)) {
            // We could checkFull for instanceof Polygonal2DRegion
            // But that currently does not match with the plugin pillar feature
            // Force cuboid
            //messages.sendMessage(player, "claim.selection.cuboid");
            context.setResult(new ActionResult(SELECTION_NOT_CUBOID, false));
            return;
        }

        final String regionName = ownerContext.getLazyHandledOwner().toHandledOwner().getRegionName();
        final ProtectedRegion selectedRegion = new ProtectedCuboidRegion(regionName, selection.getMaximumPoint(), selection.getMinimumPoint());

        if (regionValidator != null && !regionValidator.validate(context, regionManager, selectedRegion)) {
            return;
        }

        selectionProtocolHandler.handle(context, regionManager, selectedRegion);
    }

}
