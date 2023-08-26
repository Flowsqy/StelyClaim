package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.protocol.OwnerContext;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectionProtocol {

    public static final int SELECTION_NOT_DEFINED;
    public static final int SELECTION_NOT_CUBOID;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            SELECTION_NOT_DEFINED = register.get();
            SELECTION_NOT_CUBOID = register.get();
        } finally {
            register.unlock();
        }
    }

    private final SelectionProvider selectionProvider;
    private final SelectionModifier selectionModifier;
    private final RegionValidator regionValidator;
    private final SelectionProtocolHandler selectionProtocolHandler;
    private final OtherPermissionChecker permChecker;

    public SelectionProtocol(@NotNull SelectionProvider selectionProvider,
                             @Nullable SelectionModifier selectionModifier, @Nullable RegionValidator regionValidator,
                             @NotNull SelectionProtocolHandler selectionProtocolHandler,
                             @NotNull OtherPermissionChecker permChecker) {
        this.selectionProvider = selectionProvider;
        this.selectionModifier = selectionModifier;
        this.regionValidator = regionValidator;
        this.selectionProtocolHandler = selectionProtocolHandler;
        this.permChecker = permChecker;
    }

    public void process(@NotNull ActionContext context) {
        final Region selection = selectionProvider.getSelection(context);
        if (selection == null) {
            // messages.sendMessage(player, "claim.selection.empty");
            System.out.println("Selection not defined");
            context.setResult(new ActionResult(SELECTION_NOT_DEFINED, false));
            return;
        }
        if (!(context.getCustomData() instanceof final ClaimContext claimContext)) {
            throw new RuntimeException();
        }
        final OwnerContext ownerContext = claimContext.getOwnerContext();
        ownerContext.calculateOwningProperty(context.getActor(), false);
        if (!claimContext.getOwnerContext().isActorOwnTheClaim()
                && !permChecker.checkOther(context)) {
            System.out.println("Can't other");
            context.setResult(new ActionResult(InteractProtocol.CANT_OTHER, false));
            return;
        }

        final RegionManager regionManager = RegionManagerRetriever
                .retrieve(claimContext.getWorld().orElseThrow().getName());
        if (regionManager == null) {
            System.out.println("World not handled");
            context.setResult(new ActionResult(InteractProtocol.WORLD_NOT_HANDLED, false));
            return;
        }

        if (selectionModifier != null) {
            selectionModifier.modify(selection, context);
        }

        if (!(selection instanceof CuboidRegion)) {
            // We could check for instanceof Polygonal2DRegion
            // But that currently does not match with the plugin pillar feature
            // Force cuboid
            // messages.sendMessage(player, "claim.selection.cuboid");
            System.out.println("Selection not cuboid");
            context.setResult(new ActionResult(SELECTION_NOT_CUBOID, false));
            return;
        }

        final String regionName = ownerContext.getLazyHandledOwner().toHandledOwner().getRegionName();
        final ProtectedRegion selectedRegion = new ProtectedCuboidRegion(regionName, selection.getMaximumPoint(),
                selection.getMinimumPoint());

        if (regionValidator != null && !regionValidator.validate(context, regionManager, selectedRegion)) {
            System.out.println("Region not validated");
            return;
        }

        selectionProtocolHandler.handle(context, regionManager, selectedRegion);
    }

}
