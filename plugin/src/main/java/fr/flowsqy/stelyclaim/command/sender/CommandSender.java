package fr.flowsqy.stelyclaim.command.sender;

public interface CommandSender {

    org.bukkit.command.CommandSender getBukkit();

    boolean isPhysic();

    PhysicCommandSender getPhysic();

    boolean isMovable();

    MovableCommandSender getMovable();

}
