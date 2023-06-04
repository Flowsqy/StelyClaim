package fr.flowsqy.stelyclaim.api.actor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Actor {

    CommandSender getBukkit();

    default boolean isPhysic() {
        return false;
    }

    default PhysicActor getPhysic() {
        throw new UnsupportedOperationException();
    }

    default boolean isMovable() {
        return false;
    }

    default MovableActor getMovable() {
        throw new UnsupportedOperationException();
    }

    default boolean isPlayer() {
        return false;
    }

    default Player getPlayer() {
        throw new UnsupportedOperationException();
    }

}
