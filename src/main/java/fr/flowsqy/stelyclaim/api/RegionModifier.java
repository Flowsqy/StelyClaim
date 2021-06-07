package fr.flowsqy.stelyclaim.api;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public interface RegionModifier {

    void modify(ProtectedRegion region);

}
