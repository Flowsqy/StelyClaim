package fr.flowsqy.stelyclaim.api.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface PhysicActor {

    @NotNull World getWorld();

    @NotNull Location getLocation();

}
