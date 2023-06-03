package fr.flowsqy.stelyclaim.command.sender;

import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityCommandSender<T extends Entity> implements CommandSender {

    protected final T entity;

    public EntityCommandSender(@NotNull T entity) {
        this.entity = entity;
    }

    @Override
    public org.bukkit.command.CommandSender getBukkit() {
        return entity;
    }

    @Override
    public boolean isPhysic() {
        return true;
    }

    @Override
    public PhysicCommandSender getPhysic() {
        return new MovableImpl();
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public MovableCommandSender getMovable() {
        return new MovableImpl();
    }

    private class MovableImpl implements MovableCommandSender {

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