package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.*;
import fr.flowsqy.stelyclaim.command.claim.domain.AddMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.AddOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.InfoSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.RemoveSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.TeleportSubCommand;
import fr.flowsqy.stelyclaim.command.claim.selection.DefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.selection.RedefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.StatsSubCommand;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimCommand implements TabExecutor {

    private final ConfigurationFormattedMessages messages;
    private final StatisticManager statisticManager;
    private final List<SubCommand> subCommands;
    private final SubCommand helpSubCommand;
    private int tabLimit;

    public ClaimCommand(StelyClaimPlugin plugin) {
        this.messages = plugin.getMessages();
        this.statisticManager = plugin.getStatisticManager();
        subCommands = new ArrayList<>();
        initCommands(plugin);
        this.statisticManager.initSubCommands(subCommands);
        helpSubCommand = subCommands.get(0);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Get arg list
        final List<String> argsList =
                args == null ?
                        new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(args));
        // Get first arg
        final String arg =
                argsList.isEmpty() ?
                        "" :
                        argsList.get(0).toLowerCase(Locale.ROOT);
        // Check if it's a player
        final boolean isPlayer = sender instanceof Player;
        // Get matching sub-command
        final Optional<SubCommand> matchingSubCommand = getSubCommand(arg);
        // Check wrong command call and correct command call without permission
        if (matchingSubCommand.isEmpty() || !sender.hasPermission(matchingSubCommand.get().getPermission())) {
            // Send general help if the sender have the permission
            if (sender.hasPermission(helpSubCommand.getPermission())) {
                executeSubCommand(helpSubCommand, sender, Collections.singletonList(helpSubCommand.getName()), isPlayer);
            }
            return true;
        }
        // The called sub-command exist and the sender have the permission to use it
        final SubCommand calledSubCommand = matchingSubCommand.get();
        // Check if the command is executable by a non-player
        if(!isPlayer && !calledSubCommand.isConsole()) {
            return messages.sendMessage(sender, "util.onlyplayer");
        }

        executeSubCommand(calledSubCommand, sender, argsList, isPlayer);
        return true;
    }


    /**
     * Execute a sub-command
     *
     * @param command The {@link SubCommand} to execute
     * @param sender The {@link CommandSender}
     * @param argsList The {@link List} of arguments
     * @param isPlayer Whether the sender is a {@link Player}
     */
    private void executeSubCommand(SubCommand command, CommandSender sender, List<String> argsList, boolean isPlayer) {
        // Check if it's a player in a disallowed world
        if (isPlayer && !command.isAllowedInWorld(((Player) sender).getWorld().getName())){ // || sender.hasPermission("claim." + command.getName() + ".world-bypass")
            messages.sendMessage(sender, "claim.world.notallowed");
            return;
        }

        // Execute
        final boolean success = command.execute(sender, argsList, argsList.size(), isPlayer);
        // Increment and save statistics
        if (success && command.isStatistic()) {
            statisticManager.add(sender, command.getName());
            statisticManager.saveTask();
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // Get arg list
        final List<String> argsList =
                args == null ?
                        new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(args));
        // Get first arg
        final String arg =
                argsList.isEmpty() ?
                        "" :
                        argsList.get(0).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> matchingSubCommand = getSubCommand(arg);
        final boolean isPlayer = sender instanceof Player;
        if (
                matchingSubCommand.isPresent() // The first argument is a valid command
                        && argsList.size() > 1 // Typed more than one argument
                        && sender.hasPermission(matchingSubCommand.get().getPermission()) // The sender can execute (and tab) the sub-command
                        && (isPlayer || matchingSubCommand.get().isConsole()) // The sub-command can be executed by this type of sender
        ) {
            // Redirect to the sub-command
            return matchingSubCommand.get().tab(sender, argsList, isPlayer);
        } else if (argsList.size() < 2) {
            // Tab all SubCommands
            return getAvailableSubCommandNameCompletion(sender, arg, isPlayer);
        } else {
            // Wrong sub-command name and more than one argument -> do nothing
            return Collections.emptyList();
        }
    }

    /**
     * Get the sub-commands that can be viewed by a {@link CommandSender}
     *
     * @param sender   The {@link CommandSender}
     * @param isPlayer Whether the sender is a {@link Player}
     * @return A {@link Stream} of the {@link SubCommand} that the sender can view
     */
    public Stream<SubCommand> getAvailableSubCommand(CommandSender sender, boolean isPlayer) {
        // Exclude non tab commands
        Stream<SubCommand> subCommandStream = subCommands.stream().limit(tabLimit);
        // Keep only non-player ready sub-command if the sender is not a player
        if (!isPlayer) {
            subCommandStream = subCommandStream.filter(SubCommand::isConsole);
        }
        return subCommandStream.filter(subCmd -> sender.hasPermission(subCmd.getPermission())); // Check permissions
    }

    /**
     * Get the sub-command name completions
     *
     * @param sender   The {@link CommandSender}
     * @param argument The argument to compare to sub-command names and aliases
     * @param isPlayer Whether the sender is a {@link Player}
     * @return A {@link List} of possible completions for the final argument
     */
    public List<String> getAvailableSubCommandNameCompletion(CommandSender sender, String argument, boolean isPlayer) {
        Stream<String> subCommandNameStream = getAvailableSubCommand(sender, isPlayer).map(SubCommand::getName); // Just keep the name

        // If the argument is not blank, remove the one that does not start by the argument
        if (!argument.isEmpty()) {
            subCommandNameStream = subCommandNameStream.filter(cmd -> cmd.startsWith(argument));
        }
        return subCommandNameStream.collect(Collectors.toList());
    }

    /**
     * Get a sub-command from its name or its alias
     *
     * @param arg The {@link String} name or alias
     * @return An {@link Optional} sub-command that matches
     */
    private Optional<SubCommand> getSubCommand(String arg) {
        if (arg.isEmpty()) {
            return Optional.empty();
        }
        return subCommands.stream()
                .filter(cmd -> cmd.getName().equals(arg) || cmd.getAlias().equals(arg))
                .findAny();
    }

    /**
     * Initialize all internal sub-commands and fill subCommands array
     * The {@link HelpSubCommand} is the first in the list
     * Initialize the tab limit property
     *
     * @param plugin The {@link StelyClaimPlugin} instance to create all sub-commands
     */
    private void initCommands(StelyClaimPlugin plugin) {
        final Configuration config = plugin.getConfiguration();
        final HelpSubCommand helpSubCommand = new HelpSubCommand(
                plugin,
                "help",
                "h",
                Permissions.HELP,
                true,
                config.getStringList("worlds.help"),
                config.getBoolean("statistic.help"),
                this
        );
        subCommands.add(helpSubCommand);
        subCommands.add(new DefineSubCommand(
                plugin,
                "define",
                "d",
                Permissions.DEFINE,
                false,
                config.getStringList("worlds.define"),
                config.getBoolean("statistic.define")
        ));
        subCommands.add(new RedefineSubCommand(
                plugin,
                "redefine",
                "rd",
                Permissions.REDEFINE,
                false,
                config.getStringList("worlds.redefine"),
                config.getBoolean("statistic.redefine")
        ));
        subCommands.add(new AddMemberSubCommand(
                plugin,
                "addmember",
                "am",
                Permissions.ADDMEMBER,
                false,
                config.getStringList("worlds.addmember"),
                config.getBoolean("statistic.addmember")
        ));
        subCommands.add(new RemoveMemberSubCommand(
                plugin,
                "removemember",
                "rm",
                Permissions.REMOVEMEMBER,
                false,
                config.getStringList("worlds.removemember"),
                config.getBoolean("statistic.removemember")
        ));
        subCommands.add(new AddOwnerSubCommand(
                plugin,
                "addowner",
                "ao",
                Permissions.ADDOWNER,
                false,
                config.getStringList("worlds.addowner"),
                config.getBoolean("statistic.addowner")
        ));
        subCommands.add(new RemoveOwnerSubCommand(
                plugin,
                "removeowner",
                "ro",
                Permissions.REMOVEOWNER,
                false,
                config.getStringList("worlds.removeowner"),
                config.getBoolean("statistic.removeowner")
        ));
        subCommands.add(new RemoveSubCommand(
                plugin,
                "remove",
                "r",
                Permissions.REMOVE,
                false,
                config.getStringList("worlds.remove"),
                config.getBoolean("statistic.remove")
        ));
        subCommands.add(new InfoSubCommand(
                plugin,
                "info",
                "i",
                Permissions.INFO,
                false,
                config.getStringList("worlds.info"),
                config.getBoolean("statistic.info")
        ));
        subCommands.add(new TeleportSubCommand(
                plugin,
                "teleport",
                "tp",
                Permissions.TELEPORT,
                false,
                config.getStringList("worlds.teleport"),
                config.getBoolean("statistic.teleport")
        ));
        subCommands.add(new HereSubCommand(
                plugin,
                "here",
                "hr",
                Permissions.HERE,
                false,
                config.getStringList("worlds.here"),
                config.getBoolean("statistic.here")
        ));
        subCommands.add(new NearSubCommand(
                plugin,
                "near",
                "n",
                Permissions.NEAR,
                false,
                config.getStringList("worlds.near"),
                config.getBoolean("statistic.near")
        ));
        subCommands.add(new ListAddSubCommand(
                plugin,
                "listadd",
                "la",
                Permissions.LISTADD,
                false,
                config.getStringList("worlds.listadd"),
                config.getBoolean("statistic.listadd")
        ));
        final StatsSubCommand statsSubCommand = new StatsSubCommand(
                plugin,
                "stats",
                "s",
                Permissions.STATS,
                true,
                config.getBoolean("statistic.stats"),
                statisticManager
        );
        subCommands.add(statsSubCommand);
        tabLimit = subCommands.size();
        // End of tab commands
        subCommands.add(new PillarSubCommand(
                plugin,
                "pillar",
                "p",
                Permissions.PILLAR,
                false,
                config.getStringList("worlds.pillar"),
                config.getBoolean("statistic.pillar"),
                helpSubCommand
        ));
        // Load statistics tabs (should be at the end to count every sub-commands)
        statsSubCommand.initSubCommands(subCommands);
    }

    @SuppressWarnings("unused") // API
    public void registerCommand(SubCommand subCommand, boolean canTabComplete, Function<Permission, Permission> createPermissionFunction) {
        if (subCommands.stream().anyMatch(subCmd -> (
                subCmd.getName().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getName().equalsIgnoreCase(subCommand.getAlias()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getAlias())
        ))) {
            throw new IllegalArgumentException("Can not register this subCommand, the name or the alias is already taken");
        }

        Permissions.registerPerm(subCommand, createPermissionFunction);

        if (canTabComplete) {
            subCommands.add(tabLimit, subCommand);
            tabLimit++;
        } else {
            subCommands.add(subCommand);
        }
    }

    @SuppressWarnings("unused") // API
    public void unregisterCommand(SubCommand subCommand) {
        int foundIndex = -1;
        for (int index = 0; index < subCommands.size(); index++) {
            if (subCommands.get(index) == subCommand) {
                foundIndex = index;
                break;
            }
        }
        if (foundIndex > -1) {
            if (foundIndex < tabLimit) {
                tabLimit--;
            }
            subCommands.remove(foundIndex);
        }
    }

    public static class Permissions {

        public static final String HELP = "stelyclaim.claim.help";
        public static final String DEFINE = "stelyclaim.claim.define";
        public static final String REDEFINE = "stelyclaim.claim.redefine";
        public static final String ADDMEMBER = "stelyclaim.claim.addmember";
        public static final String REMOVEMEMBER = "stelyclaim.claim.removemember";
        public static final String ADDOWNER = "stelyclaim.claim.addowner";
        public static final String REMOVEOWNER = "stelyclaim.claim.removeowner";
        public static final String REMOVE = "stelyclaim.claim.remove";
        public static final String INFO = "stelyclaim.claim.info";
        public static final String TELEPORT = "stelyclaim.claim.teleport";
        public static final String HERE = "stelyclaim.claim.here";
        public static final String NEAR = "stelyclaim.claim.near";
        public static final String LISTADD = "stelyclaim.claim.listadd";
        public static final String STATS = "stelyclaim.claim.stats";
        public static final String PILLAR = "stelyclaim.claim.pillar";

        public static void registerPerm(SubCommand subCommand, Function<Permission, Permission> createPermissionFunction) {
            // Get base and global perm
            final Permission globalPerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim.*");
            final Permission basePerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim");
            if (globalPerm == null || basePerm == null) {
                throw new RuntimeException(
                        "Can not register '"
                                + subCommand.getName()
                                + "' subcommand because global perm or base perm are not registered"
                );
            }
            // Create permission
            final Permission commandPerm = new Permission(subCommand.getPermission());
            // Link the command permission to the base permission
            basePerm.addParent(commandPerm, true);

            // Create custom permissions
            final Permission topPermission = createPermissionFunction.apply(commandPerm);
            // Link the top permission to the global permission
            topPermission.addParent(globalPerm, true);
        }

        public static String getOtherPerm(String permission) {
            return permission + "-other";
        }

    }

}
