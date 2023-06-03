package fr.flowsqy.stelyclaim.command.sender;

import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface MovableCommandSender extends PhysicCommandSender {

    void setLocation(@NotNull TeleportSync teleportSync, @NotNull Location location);

}
