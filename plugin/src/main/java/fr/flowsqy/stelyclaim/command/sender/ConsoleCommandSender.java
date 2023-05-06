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

    @Override
    public boolean isPhysic() {
        return false;
    }

    @Override
    public PhysicCommandSender getPhysic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public MovableCommandSender getMovable() {
        throw new UnsupportedOperationException();
    }
}
