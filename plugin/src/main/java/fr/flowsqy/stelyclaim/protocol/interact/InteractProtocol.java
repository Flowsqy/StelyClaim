package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.api.*;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import fr.flowsqy.stelyclaim.protocol.RegionManagerRetriever;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InteractProtocol {

    // Results
    public static final int CANT_OTHER = ActionResult.registerResultCode();
    public static final int WORLD_NOT_HANDLED = ActionResult.registerResultCode();
    public static final int REGION_NOT_EXIST = ActionResult.registerResultCode();
    public static final int TRY_INTERACT_GLOBAL = ActionResult.registerResultCode();


    /*
    private final InteractProtocolHandler removeProtocolHandler;

    public InteractProtocol(StelyClaimPlugin plugin) {
        removeProtocolHandler = new RemoveHandler(plugin);
    }

    public InteractProtocolHandler getRemoveProtocolHandler() {
        return removeProtocolHandler;
    }*/

    public void process(@NotNull ActionContext<ClaimContextData> context, @NotNull InteractProtocolHandler interactProtocolHandler) {
        //final T owner = handledOwner.owner();
        //final ClaimHandler<T> handler = handledOwner.handler();
        //final HandlerMessages messages = handler.getClaimInteractHandler().getMessages();

        //final CommandSender sender = actor.getBukkit();
        //final boolean ownRegion = owner.own(actor);

        if (!Objects.requireNonNull(context.getCustomData()).own() && interactProtocolHandler.canInteractNotOwned(context)
            /*!sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(interactProtocolHandler.getPermission())) */
        ) {
            context.setResult(new ActionResult(CANT_OTHER, false));
            return;
            //messages.sendMessage(sender, "help." + interactProtocolHandler.getName());
            //return false;
        }

        final RegionManager regionManager = RegionManagerRetriever.retrieve(world.getName());
        if (regionManager == null) {
            context.setResult(new ActionResult(WORLD_NOT_HANDLED, false));
            return;
        }

        final String regionName = RegionNameManager.getRegionName(handledOwner);

        final ProtectedRegion region = regionManager.getRegion(regionName);
        //RegionNameManager.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (region == null) {
            context.setResult(new ActionResult(REGION_NOT_EXIST, false));
            return;
        }

        if (region.getType() == RegionType.GLOBAL && !interactProtocolHandler.canInteractGlobal(context)) {
            //TODO Maybe general instead of specific message
            //messages.sendMessage(sender, "claim.interactglobal");
            context.setResult(new ActionResult(TRY_INTERACT_GLOBAL, false));
            return;
        }

        interactProtocolHandler.interactRegion(regionManager, region, context);
    }

}
