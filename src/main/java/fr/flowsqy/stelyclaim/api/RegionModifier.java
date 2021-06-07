package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public interface RegionModifier<T extends ClaimOwner> {

    void modify(ProtectedRegion region, T claimOwner);

}
