package fr.flowsqy.stelyclaim.api.actor;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleActor implements Actor {

    private final ConsoleCommandSender console;

    public ConsoleActor(ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public CommandSender getBukkit() {
        return console;
    }

}
