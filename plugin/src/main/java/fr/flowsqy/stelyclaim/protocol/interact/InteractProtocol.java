package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.protocol.OwnerContext;
import fr.flowsqy.stelyclaim.protocol.ProtocolInteractChecker;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;

public class InteractProtocol {

    // Results
    public static final int CANT_OTHER;
    public static final int WORLD_NOT_HANDLED;
    public static final int REGION_NOT_EXIST;
    public static final int TRY_INTERACT_GLOBAL;
    public static final int SUCCESS;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            CANT_OTHER = register.get();
            WORLD_NOT_HANDLED = register.get();
            REGION_NOT_EXIST = register.get();
            TRY_INTERACT_GLOBAL = register.get();
            SUCCESS = register.get();
        } finally {
            register.unlock();
        }
    }


    /*
    private final InteractProtocolHandler removeProtocolHandler;

    public InteractProtocol(StelyClaimPlugin plugin) {
        removeProtocolHandler = new RemoveHandler(plugin);
    }

    public InteractProtocolHandler getRemoveProtocolHandler() {
        return removeProtocolHandler;
    }*/

    private final @NotNull InteractProtocolHandler interactProtocolHandler;
    private final @NotNull OtherPermissionChecker permChecker;

    public InteractProtocol(@NotNull InteractProtocolHandler interactProtocolHandler, @NotNull OtherPermissionChecker permChecker) {
        this.interactProtocolHandler = interactProtocolHandler;
        this.permChecker = permChecker;
    }

    public void process(@NotNull ActionContext context) {
        //final T owner = handledOwner.owner();
        //final ClaimHandler<T> handler = handledOwner.handler();
        //final HandlerMessages messages = handler.getClaimInteractHandler().getMessages();

        //final CommandSender sender = actor.getBukkit();
        //final boolean ownRegion = owner.own(actor);
        if (!(context.getCustomData() instanceof ClaimContext claimContext)) {
            throw new RuntimeException();
        }
        final OwnerContext ownerContext = claimContext.getOwnerContext();
        ownerContext.calculateOwningProperty(context.getActor(), false);
        if (!ownerContext.isActorOwnTheClaim() && !permChecker.checkOther(context)
            /*!sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(interactProtocolHandler.getPermission())) */
        ) {
            context.setResult(new ActionResult(CANT_OTHER, false));
            return;
            //messages.sendMessage(sender, "help." + interactProtocolHandler.getName());
            //return false;
        }

        final RegionManager regionManager = RegionManagerRetriever.retrieve(claimContext.getWorld().orElseThrow().getName());
        if (regionManager == null) {
            context.setResult(new ActionResult(WORLD_NOT_HANDLED, false));
            return;
        }

        final String regionName = ownerContext.getLazyHandledOwner().toHandledOwner().getRegionName();

        final ProtectedRegion region = regionManager.getRegion(regionName);
        //RegionNameManager.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (region == null) {
            context.setResult(new ActionResult(REGION_NOT_EXIST, false));
            return;
        }

        if (region.getType() == RegionType.GLOBAL /*&& !protocolInteractChecker.canInteractGlobal(context)*/) {
            //TODO Maybe general instead of specific message
            //messages.sendMessage(sender, "claim.interactglobal");
            context.setResult(new ActionResult(TRY_INTERACT_GLOBAL, false));
            return;
        }

        interactProtocolHandler.interactRegion(regionManager, region, context);
    }

}
