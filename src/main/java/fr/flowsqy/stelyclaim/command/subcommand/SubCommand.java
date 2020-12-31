package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    protected final StelyClaimPlugin plugin;
    protected final Messages messages;
    protected final String name;
    protected final String alias;
    protected final String permission;
    protected final boolean stats;
    protected final boolean console;

    public SubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.name = name;
        this.alias = alias;
        this.permission = permission;
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

    public boolean isStats() {
        return stats;
    }

    public boolean isConsole() {
        return console;
    }

    public abstract void execute(CommandSender sender, List<String> args, boolean isPlayer);

    public abstract List<String> tab(CommandSender sender, List<String> args, boolean isPlayer);

}