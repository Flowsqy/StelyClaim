package fr.flowsqy.stelyclaim.pillar;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PillarSession {

    private final static byte LOCATION_MAX_INDEX = 9;
    private final Location[] locations;
    private int nextPillarId;

    public PillarSession() {
        this.locations = new Location[LOCATION_MAX_INDEX + 1];
        this.nextPillarId = -1;
    }

    public int next() {
        nextPillarId++;
        if (nextPillarId > LOCATION_MAX_INDEX)
            nextPillarId = 0;
        return nextPillarId;
    }

    public int registerLocation(@NotNull Location location) {
        final int index = next();
        locations[index] = location;
        return index;
    }

    @Nullable
    public Location get(int index) {
        if (index < 0 || index > 9) {
            throw new IllegalArgumentException();
        }
        return locations[index];
    }
}
