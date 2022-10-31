package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class SubCommand {

    protected final ConfigurationFormattedMessages messages;
    protected final String name;
    protected final String alias;
    protected final String permission;
    protected final boolean console;
    protected final Set<String> allowedWorlds;
    protected final boolean statistic;

    public SubCommand(ConfigurationFormattedMessages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        Objects.requireNonNull(messages);
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

    /**
     * Whether this sub-command can be executed by a sender that is not a player
     *
     * @return {@code true} if the command can be sent by a non-player sender, {@code false} otherwise
     */
    public boolean isConsole() {
        return console;
    }

    /**
     * Whether this sub-command is allowed in a world
     *
     * @param worldName The name of the world to check
     * @return {@code true} if the sub-command is allowed, {@code false} otherwise
     */
    public boolean isAllowedInWorld(String worldName) {
        // If the list is empty, all world are allowed
        if (allowedWorlds.isEmpty()) {
            return true;
        }
        // No world so blocked by default
        if (worldName == null) {
            return false;
        }
        return allowedWorlds.contains(worldName);
    }

    /**
     * Whether the sub-command is tracked by the {@link fr.flowsqy.stelyclaim.io.StatisticManager}
     *
     * @return {@code true} if the command is tracked by the {@link fr.flowsqy.stelyclaim.io.StatisticManager}, {@code false} otherwise
     */
    public boolean isStatistic() {
        return statistic;
    }

    /**
     * Get the help message for a specific {@link CommandSender}
     *
     * @param sender The {@link CommandSender}
     * @return The help message that should be displayed
     */
    public abstract String getHelpMessage(CommandSender sender);

    /**
     * Execute this sub-command
     *
     * @param sender   The {@link CommandSender}
     * @param args     The argument {@link List}
     * @param size     The size of the argument list
     * @param isPlayer Whether the sender is a {@link org.bukkit.entity.Player}
     * @return {@code true} if the command succeed, {@code false} otherwise
     */
    public abstract boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer);

    /**
     * Tab this sub-command
     *
     * @param sender   The {@link CommandSender}
     * @param args     The argument {@link List}
     * @param isPlayer Whether the sender is a {@link org.bukkit.entity.Player}
     * @return A {@link List} of possible completions for the final argument
     */
    public abstract List<String> tab(CommandSender sender, List<String> args, boolean isPlayer);

}