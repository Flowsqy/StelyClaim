package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public interface RegionModifier<T extends ClaimOwner> {

    void modify(Player sender, ProtectedRegion region, T claimOwner);

}
