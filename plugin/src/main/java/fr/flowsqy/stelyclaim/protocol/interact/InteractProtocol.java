package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.api.*;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InteractProtocol {

    // Results
    public static final int CANT_OTHER = ActionResult.registerResultCode();
    public static final int WORLD_NOT_HANDLED = ActionResult.registerResultCode();
    public static final int REGION_NOT_EXIST = ActionResult.registerResultCode();
    public static final int TRY_INTERACT_GLOBAL = ActionResult.registerResultCode();

    // Modifiers
    public static final int MOD_OWN = 0b1;


    /*
    private final InteractProtocolHandler removeProtocolHandler;

    public InteractProtocol(StelyClaimPlugin plugin) {
        removeProtocolHandler = new RemoveHandler(plugin);
    }

    public InteractProtocolHandler getRemoveProtocolHandler() {
        return removeProtocolHandler;
    }*/

    @NotNull
    public <T extends ClaimOwner> ActionResult process(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> handledOwner, @NotNull InteractProtocolHandler interactProtocolHandler) {
        final T owner = handledOwner.owner();
        final ClaimHandler<T> handler = handledOwner.handler();
        //final HandlerMessages messages = handler.getClaimInteractHandler().getMessages();

        final CommandSender sender = actor.getBukkit();
        final boolean ownRegion = owner.own(actor);

        // TODO Handle OtherPerm check
        if (!ownRegion && interactProtocolHandler.canInteractOther(handler.getClaimInteractHandler().getInteractChecker()).canInteractOther() // TODO Specify protocol
            /*!sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(interactProtocolHandler.getPermission())) */
        ) {
            return new ActionResult(CANT_OTHER, false);
            //messages.sendMessage(sender, "help." + interactProtocolHandler.getName());
            //return false;
        }

        // TODO Handle this better
        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(world.getName()), sender);
        if (regionManager == null) {
            return new ActionResult(WORLD_NOT_HANDLED, false);
        }

        final String regionName = RegionFinder.getRegionName(handler, owner);

        final ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor);
        if (region == null) {
            return new ActionResult(REGION_NOT_EXIST, false, ownRegion ? MOD_OWN : 0);
        }

        if (region.getType() == RegionType.GLOBAL) {
            //TODO Maybe general instead of specific
            //messages.sendMessage(sender, "claim.interactglobal");
            return new ActionResult(TRY_INTERACT_GLOBAL, false);
        }

        return interactProtocolHandler.interactRegion(regionManager, region, handledOwner, actor, ownRegion);
    }

}
