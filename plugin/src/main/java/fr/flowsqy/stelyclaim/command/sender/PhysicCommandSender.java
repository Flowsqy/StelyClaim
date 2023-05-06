package fr.flowsqy.stelyclaim.command.sender;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface PhysicCommandSender {

    @NotNull World getWorld();

    @NotNull Location getLocation();

}
