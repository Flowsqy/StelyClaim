package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.*;
import fr.flowsqy.stelyclaim.command.subcommand.domain.AddMemberSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.domain.AddOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.domain.RemoveMemberSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.domain.RemoveOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.interact.InfoSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.interact.RemoveSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.interact.TeleportSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.selection.DefineSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.selection.RedefineSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.statistics.StatsSubCommand;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
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

    public int getTabLimit() {
        return tabLimit;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final List<String> argsList =
                args == null ?
                        new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(args));
        final String arg =
                argsList.size() < 1 ?
                        "" :
                        argsList.get(0).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> subCommand = getSubCommand(arg);
        final boolean isPlayer = sender instanceof Player;
        if (subCommand.isPresent()) {
            // Redirect to SubCommand's executable
            final SubCommand subCmd = subCommand.get();
            if (isPlayer ? sender.hasPermission(subCmd.getPermission()) : subCmd.isConsole()) {
                executeSubCommand(subCmd, sender, argsList, isPlayer);
                return true;
            }
            if (!isPlayer)
                return messages.sendMessage(sender, "util.onlyplayer");
        }
        if (sender.hasPermission(helpSubCommand.getPermission())) {
            // Send help if has perm
            executeSubCommand(helpSubCommand, sender, argsList, isPlayer);
        }
        return true;
    }

    private void executeSubCommand(SubCommand command, CommandSender sender, List<String> argsList, boolean isPlayer) {
        final Set<String> allowedWorlds = command.getAllowedWorlds();
        if (
                !(sender instanceof Player) ||
                        allowedWorlds.isEmpty() ||
                        allowedWorlds.contains(((Player) sender).getWorld().getName())
            // || sender.hasPermission("claim." + command.getName() + ".world-bypass")
        ) {
            final boolean success = command.execute(sender, argsList, argsList.size(), isPlayer);
            if (success && command.isStatistic()) {
                statisticManager.add(sender, command.getName());
                statisticManager.saveTask();
            }
            return;
        }
        messages.sendMessage(sender, "claim.world.notallowed");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> argsList =
                args == null ?
                        new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(args));
        final String arg =
                argsList.size() < 1 ?
                        "" :
                        argsList.get(0).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> subCommand = getSubCommand(arg);
        if (subCommand.isPresent()) {
            // Tab a SubCommand
            final SubCommand subCmd = subCommand.get();
            final boolean isPlayer = sender instanceof Player;
            if (isPlayer ? sender.hasPermission(subCmd.getPermission()) : subCmd.isConsole()) {
                return subCmd.tab(sender, argsList, isPlayer);
            } else {
                return Collections.emptyList();
            }
        } else if (argsList.size() < 2) {
            // Tab all SubCommands
            final Stream<SubCommand> subCommandStream;
            if (sender instanceof Player)
                subCommandStream = subCommands.stream()
                        .limit(tabLimit)  // Exclude non tab commands
                        .filter(cmd -> sender.hasPermission(cmd.getPermission()));
            else {
                subCommandStream = subCommands.stream()
                        .limit(tabLimit)  // Exclude non tab commands
                        .filter(SubCommand::isConsole);
            }
            if (arg.isEmpty())
                return subCommandStream
                        .map(SubCommand::getName)
                        .collect(Collectors.toList());
            else
                return subCommandStream
                        .map(SubCommand::getName)
                        .filter(cmd -> cmd.startsWith(arg))
                        .collect(Collectors.toList());
        } else
            // Wrong subCommandName, do nothing
            return Collections.emptyList();
    }

    private Optional<SubCommand> getSubCommand(String arg) {
        if (arg.isEmpty())
            return Optional.empty();
        return subCommands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(arg) || cmd.getAlias().equalsIgnoreCase(arg))
                .findAny();
    }

    private void initCommands(StelyClaimPlugin plugin) {
        // SubCommands : help, define, redefine, addmember, removemember, addowner, removeowner, remove, info, teleport, stats, pillar
        final Configuration config = plugin.getConfiguration();
        final HelpSubCommand helpSubCommand = new HelpSubCommand(
                plugin,
                "help",
                "h",
                Permissions.HELP,
                true,
                config.getStringList("worlds.help"),
                config.getBoolean("statistic.help"),
                subCommands,
                this::getTabLimit
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
        statsSubCommand.initSubCommands(subCommands);
    }

    public void registerCommand(SubCommand subCommand, boolean canTabComplete) {
        if (subCommands.stream().anyMatch(subCmd -> (
                subCmd.getName().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getName().equalsIgnoreCase(subCommand.getAlias()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getAlias())
        ))) {
            throw new IllegalArgumentException("Can not register this subCommand, the name or the aliase is already taken");
        }

        Permissions.registerPerm(subCommand);

        if (canTabComplete) {
            subCommands.add(tabLimit, subCommand);
            tabLimit++;
        } else {
            subCommands.add(subCommand);
        }
    }

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

        public static void registerPerm(SubCommand subCommand) {
            final Permission globalPerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim.*");
            final Permission basePerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim");
            if (globalPerm == null || basePerm == null)
                throw new RuntimeException(
                        "Can not register '"
                                + subCommand.getName()
                                + "' subcommand because global perm or base perm are not registered"
                );
            final Permission commandPerm = new Permission(subCommand.getPermission());
            final Permission commandOtherPerm = new Permission(subCommand.getOtherPermission());

            basePerm.addParent(commandPerm, true);
            commandPerm.addParent(commandOtherPerm, true);
            commandOtherPerm.addParent(globalPerm, true);
        }

        public static String getOtherPerm(String permission) {
            return permission + "-other";
        }

    }

}
