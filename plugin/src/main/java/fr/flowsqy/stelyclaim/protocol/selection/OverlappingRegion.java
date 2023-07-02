package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class OverlappingRegion {

    private ProtectedRegion[] regionsOverlapping;
    private ProtectedRegion overlapSameId;

    public void process(@NotNull RegionManager regionManager, @NotNull ProtectedRegion selectedRegion) {
        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(selectedRegion);
        final List<ProtectedRegion> overlappingList = new LinkedList<>();
        for (ProtectedRegion region : intersecting) {
            if (region.getId().equals(selectedRegion.getId())) {
                overlapSameId = region;
                continue;
            }
            overlappingList.add(region);
        }
        regionsOverlapping = overlappingList.toArray(new ProtectedRegion[0]);
    }

    public boolean isProcessed() {
        return regionsOverlapping != null;
    }

    @NotNull
    public ProtectedRegion[] getRegionsOverlapping() {
        if (!isProcessed()) {
            throw new IllegalStateException();
        }
        return regionsOverlapping;
    }

    @Nullable
    public ProtectedRegion getOverlapSameId() {
        if (!isProcessed()) {
            throw new IllegalStateException();
        }
        return overlapSameId;
    }

}
