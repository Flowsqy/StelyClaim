package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    private final String name;
    private final String alias;
    private final String permission;
    private final boolean other;
    private final boolean stats;
    private final boolean console;

    public SubCommand(String name, String alias, String permission, boolean other, boolean stats, boolean console) {
        this.name = name;
        this.alias = alias;
        this.permission = permission;
        this.other = other;
        this.stats = stats;
        this.console = console;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isOther() {
        return other;
    }

    public boolean isStats() {
        return stats;
    }

    public boolean isConsole() {
        return console;
    }

    public abstract void execute(CommandSender sender, String arg, List<String> args);

    public abstract List<String> tab(CommandSender sender, String arg, List<String> args);

}