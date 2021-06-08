package fr.flowsqy.stelyclaim.protocol;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.bukkit.entity.Player;

public class RegionFinder {

    public static <T extends ClaimOwner> String getRegionName(ClaimHandler<T> handler, T owner) {
        return "stelyclaim_" + handler.getId() + "_" + handler.getIdentifier(owner);
    }

    public static RegionManager getRegionManager(World world, Player sender, ClaimMessage messages) {
        final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) {
            messages.sendMessage(sender,
                    "claim.world.nothandle",
                    "%world%", world.getName());
        }
        return regionManager;
    }

    public static ProtectedRegion mustExist(RegionManager manager, String regionName, boolean ownRegion, Player sender, ClaimMessage messages) {
        final ProtectedRegion region = manager.getRegion(regionName);
        if (region == null) {
            messages.sendMessage(sender, "claim.exist.not" + (ownRegion ? "" : "-other"), "%region%", regionName);
        }
        return region;
    }

    public static boolean mustNotExist(RegionManager manager, String regionName, boolean ownRegion, Player sender, ClaimMessage messages) {
        final boolean exist = manager.getRegion(regionName) != null;
        if (exist) {
            messages.sendMessage(sender, "claim.exist.already" + (ownRegion ? "" : "-other"), "%region%", regionName);
        }
        return exist;
    }


}
