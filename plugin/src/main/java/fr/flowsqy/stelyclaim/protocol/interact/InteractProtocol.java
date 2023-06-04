package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.*;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InteractProtocol {

    private final InteractProtocolHandler removeProtocolHandler;

    public InteractProtocol(StelyClaimPlugin plugin) {
        removeProtocolHandler = new RemoveHandler(plugin);
    }

    public InteractProtocolHandler getRemoveProtocolHandler() {
        return removeProtocolHandler;
    }

    public <T extends ClaimOwner> boolean process(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> handledOwner, @NotNull InteractProtocolHandler interactProtocolHandler) {
        final T owner = handledOwner.owner();
        final ClaimHandler<T> handler = handledOwner.handler();
        final FormattedMessages messages = handler.getMessages();

        final CommandSender sender = actor.getBukkit();
        final boolean ownRegion = actor.isPlayer() && owner.own(actor.getPlayer());

        if (!ownRegion && !sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(interactProtocolHandler.getPermission()))) {
            messages.sendMessage(sender, "help." + interactProtocolHandler.getName());
            return false;
        }

        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(world.getName()), sender, messages);
        if (regionManager == null)
            return false;

        final String regionName = RegionFinder.getRegionName(handler, owner);

        final ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor, messages);
        if (region == null)
            return false;

        if (region.getType() == RegionType.GLOBAL) {
            messages.sendMessage(sender, "claim.interactglobal");
            return false;
        }

        return interactProtocolHandler.interactRegion(regionManager, region, ownRegion, handledOwner, actor, messages);
    }

}
