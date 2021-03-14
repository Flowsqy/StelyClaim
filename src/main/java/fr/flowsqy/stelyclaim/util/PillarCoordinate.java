package fr.flowsqy.stelyclaim.util;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

public class PillarCoordinate {

    private final World world;
    private final int maxX;
    private final int minX;
    private final int maxZ;
    private final int minZ;

    public PillarCoordinate(ProtectedRegion region, World world) {
        this.world = world;
        final BlockVector3 maxPoint = region.getMaximumPoint();
        final BlockVector3 minPoint = region.getMinimumPoint();
        final int maxX = maxPoint.getBlockX();
        final int maxZ = maxPoint.getBlockZ();
        final int minX = minPoint.getBlockX();
        final int minZ = minPoint.getBlockZ();
        if(maxX > minX){
            this.maxX = maxX;
            this.minX = minX;
        }
        else{
            this.maxX = minX;
            this.minX = maxX;
        }
        if(maxZ > minZ){
            this.maxZ = maxZ;
            this.minZ = minZ;
        }
        else{
            this.maxZ = minZ;
            this.minZ = maxZ;
        }
    }

    public World getWorld() {
        return world;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinZ() {
        return minZ;
    }

    public Location getNorthWestBlockLocation(){
        return new Location(world, minX, 0, minZ, -45, 0);
    }

    public Location getNorthEastBlockLocation(){
        return new Location(world, maxX, 0, minZ, 45, 0);
    }

    public Location getSouthWestBlockLocation(){
        return new Location(world, minX, 0, maxZ, -135, 0);
    }

    public Location getSouthEastBlockLocation(){
        return new Location(world, maxX, 0, maxZ, 135, 0);
    }

    public Location getNorthWestLocation(){
        return getTeleportLocation(getNorthWestBlockLocation());
    }

    public Location getNorthEastLocation(){
        return getTeleportLocation(getNorthEastBlockLocation());
    }

    public Location getSouthWestLocation(){
        return getTeleportLocation(getSouthWestBlockLocation());
    }

    public Location getSouthEastLocation(){
        return getTeleportLocation(getSouthEastBlockLocation());
    }
    
    public Location getTeleportLocation(Location blockLocation){
        return blockLocation.add(0.5, world.getHighestBlockYAt(blockLocation) + 1, 0.5);
    }

}
