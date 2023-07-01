package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface InteractProtocolHandler {

    <T extends ClaimOwner> void interactRegion(
            @NotNull RegionManager regionManager,
            @NotNull ProtectedRegion region,
            @NotNull ActionContext<ClaimContextData> context
    );

    boolean canInteractNotOwned(@NotNull ActionContext<ClaimContextData> context);

    boolean canInteractGlobal(@NotNull ActionContext<ClaimContextData> context);

}
