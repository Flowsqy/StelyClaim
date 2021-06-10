package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class InteractProtocol {

    public static <T extends ClaimOwner> boolean process(World world, Player sender, ClaimHandler<T> handler, T owner, InteractProtocolHandler interactProtocolHandler) {
        final ClaimMessage messages = handler.getMessages();

        final boolean ownRegion = owner.own(sender);

        if (!ownRegion && !sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(interactProtocolHandler.getPermission()))) {
            messages.sendMessage(sender, "help." + interactProtocolHandler.getName());
            return false;
        }

        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(world.getName()), sender, messages);
        if (regionManager == null)
            return false;

        final String regionName = RegionFinder.getRegionName(handler, owner);

        final ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, ownRegion, sender, messages);
        if (region == null)
            return false;

        if (region.getType() == RegionType.GLOBAL) {
            messages.sendMessage(sender, "claim.interactglobal");
            return false;
        }

        return interactProtocolHandler.interactRegion(regionManager, region, ownRegion, handler, owner, sender, messages);
    }

}
