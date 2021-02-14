package fr.flowsqy.stelyclaim.util;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class PillarData {

    private final Map<String, Location> locations;
    private int nextPillarId;

    public PillarData() {
        this.locations = new HashMap<>();
        this.nextPillarId = -1;
    }

    public int getNextPillarId() {
        return nextPillarId;
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public int next(){
        nextPillarId++;
        if(nextPillarId > 9)
            nextPillarId = 0;
        return nextPillarId;
    }

    public String registerLocation(Location location){
        final String id = String.valueOf(nextPillarId);
        locations.put(id, location);
        return id;
    }

}
