package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class SubCommand {

    protected final StelyClaimPlugin plugin;
    protected final Messages messages;
    protected final String name;
    protected final String alias;
    protected final String permission;
    protected final boolean console;
    protected final Set<String> allowedWorlds;
    protected final boolean statistic;

    public SubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        this(plugin, plugin.getMessages(), name, alias, permission, console, allowedWorlds, statistic);
    }

    public SubCommand(StelyClaimPlugin plugin, Messages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        Objects.requireNonNull(messages);
        this.plugin = plugin;
        this.messages = messages;
        this.name = name;
        this.alias = alias;
        this.permission = permission;
        this.console = console;
        this.allowedWorlds = new HashSet<>(allowedWorlds);
        this.statistic = statistic;
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

    public String getOtherPermission() {
        return ClaimCommand.Permissions.getOtherPerm(permission);
    }

    public boolean isConsole() {
        return console;
    }

    public Set<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    public boolean isStatistic() {
        return statistic;
    }

    public abstract boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer);

    public abstract List<String> tab(CommandSender sender, List<String> args, boolean isPlayer);

}