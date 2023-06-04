package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface InteractProtocolHandler {

    String getPermission();

    String getName();

    <T extends ClaimOwner> boolean interactRegion(
            RegionManager regionManager,
            ProtectedRegion region,
            boolean ownRegion,
            HandledOwner<T> owner,
            Actor actor,
            FormattedMessages messages
    );

}
