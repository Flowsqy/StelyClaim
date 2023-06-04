package fr.flowsqy.stelyclaim.api.actor;

import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface MovableActor extends PhysicActor {

    void setLocation(@NotNull TeleportSync teleportSync, @NotNull Location location);

}
