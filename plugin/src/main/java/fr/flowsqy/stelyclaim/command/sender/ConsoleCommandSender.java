package fr.flowsqy.stelyclaim.command.sender;

public class ConsoleCommandSender implements CommandSender {

    private final org.bukkit.command.ConsoleCommandSender console;

    public ConsoleCommandSender(org.bukkit.command.ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public org.bukkit.command.CommandSender getBukkit() {
        return console;
    }

}
