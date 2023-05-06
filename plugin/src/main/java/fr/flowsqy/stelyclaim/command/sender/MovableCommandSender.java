package fr.flowsqy.stelyclaim.command.sender;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface MovableCommandSender extends PhysicCommandSender {

    boolean setLocation(@NotNull Location location);

}
