package fr.flowsqy.stelyclaim.command.sender;

import org.bukkit.entity.Player;

public interface CommandSender {

    org.bukkit.command.CommandSender getBukkit();

    default boolean isPhysic() {
        return false;
    }

    default PhysicCommandSender getPhysic() {
        throw new UnsupportedOperationException();
    }

    default boolean isMovable() {
        return false;
    }

    default MovableCommandSender getMovable() {
        throw new UnsupportedOperationException();
    }

    default boolean isPlayer() {
        return false;
    }

    default Player getPlayer() {
        throw new UnsupportedOperationException();
    }

}
