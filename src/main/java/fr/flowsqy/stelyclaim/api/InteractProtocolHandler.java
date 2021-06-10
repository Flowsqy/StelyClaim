package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public interface InteractProtocolHandler {

    String getPermission();

    String getName();

    <T extends ClaimOwner> boolean interactRegion(
            RegionManager regionManager,
            ProtectedRegion region,
            boolean ownRegion,
            ClaimHandler<T> handler,
            T owner,
            Player sender,
            ClaimMessage messages
    );

}
