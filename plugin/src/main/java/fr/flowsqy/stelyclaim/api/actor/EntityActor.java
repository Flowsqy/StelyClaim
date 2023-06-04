package fr.flowsqy.stelyclaim.api.actor;

import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityActor<T extends Entity> implements Actor {

    protected final T entity;

    public EntityActor(@NotNull T entity) {
        this.entity = entity;
    }

    @Override
    public CommandSender getBukkit() {
        return entity;
    }

    @Override
    public boolean isPhysic() {
        return true;
    }

    @Override
    public PhysicActor getPhysic() {
        return new MovableImpl();
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public MovableActor getMovable() {
        return new MovableImpl();
    }

    private class MovableImpl implements MovableActor {

        @Override
        public void setLocation(@NotNull TeleportSync teleportSync, @NotNull Location location) {
            teleportSync.addTeleport(entity, location);
        }

        @Override
        public @NotNull World getWorld() {
            return entity.getWorld();
        }

        @Override
        public @NotNull Location getLocation() {
            return entity.getLocation();
        }
    }

}
