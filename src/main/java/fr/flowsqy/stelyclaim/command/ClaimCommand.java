package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.HelpSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.PillarSubCommand;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
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
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimCommand implements TabExecutor {

    private final Messages messages;
    private final StatisticManager statisticManager;
    private final List<SubCommand> subCommands;
    private final SubCommand helpSubCommand;

    public ClaimCommand(StelyClaimPlugin plugin){
        this.messages = plugin.getMessages();
        this.statisticManager = plugin.getStatisticManager();
        subCommands = new ArrayList<>();
        initCommands(plugin);
        this.statisticManager.initSubCommands(subCommands);
        helpSubCommand = subCommands.get(0);
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
        if(subCommand.isPresent()) {
            // Redirect to SubCommand's executable
            final SubCommand subCmd = subCommand.get();
            if (isPlayer ? sender.hasPermission(subCmd.getPermission()) : subCmd.isConsole()){
                executeSubCommand(subCmd, sender, argsList, isPlayer);
                return true;
            }
            if(!isPlayer)
                return messages.sendMessage(sender, "util.onlyplayer");
        }
        if(sender.hasPermission(helpSubCommand.getPermission())) {
            // Send help if has perm
            executeSubCommand(helpSubCommand, sender, argsList, isPlayer);
        }
        return true;
    }

    private void executeSubCommand(SubCommand command, CommandSender sender, List<String> argsList, boolean isPlayer){
        final Set<String> allowedWorlds = command.getAllowedWorlds();
        if(
                !(sender instanceof Player) ||
                allowedWorlds.isEmpty() ||
                allowedWorlds.contains(((Player) sender).getWorld().getName())
                // || sender.hasPermission("claim." + command.getName() + ".world-bypass")
        ){
            command.execute(sender, argsList, argsList.size(), isPlayer);
            if(command.isStatistic()) {
                statisticManager.add(sender, command.getName());
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
        if(subCommand.isPresent()) {
            // Tab a SubCommand
            final SubCommand subCmd = subCommand.get();
            final boolean isPlayer = sender instanceof Player;
            if (isPlayer ? sender.hasPermission(subCmd.getPermission()) : subCmd.isConsole()) {
                return subCmd.tab(sender, argsList, isPlayer);
            }
            else {
                return Collections.emptyList();
            }
        }
        else if (argsList.size() < 2) {
            // Tab all SubCommands
            final Stream<SubCommand> subCommandStream;
            if(sender instanceof Player)
                subCommandStream = subCommands.stream()
                        .limit(11)  // Exclude Pillar
                        .filter(cmd -> sender.hasPermission(cmd.getPermission()));
            else {
                subCommandStream = subCommands.stream()
                        .limit(11)  // Exclude Pillar
                        .filter(SubCommand::isConsole);
            }
            if(arg.isEmpty())
                return subCommandStream
                        .map(SubCommand::getName)
                        .collect(Collectors.toList());
            else
                return subCommandStream
                        .map(SubCommand::getName)
                        .filter(cmd -> cmd.startsWith(arg))
                        .collect(Collectors.toList());
        }
        else
            // Wrong subCommandName, do nothing
            return Collections.emptyList();
    }

    private Optional<SubCommand> getSubCommand(String arg){
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
                "stelyclaim.claim.help",
                true,
                config.getStringList("worlds.help"),
                config.getBoolean("statistic.help"),
                subCommands
        );
        subCommands.add(helpSubCommand);
        subCommands.add(new DefineSubCommand(
                plugin,
                "define",
                "d",
                "stelyclaim.claim.define",
                false,
                config.getStringList("worlds.define"),
                config.getBoolean("statistic.define")
        ));
        subCommands.add(new RedefineSubCommand(
                plugin,
                "redefine",
                "rd",
                "stelyclaim.claim.redefine",
                false,
                config.getStringList("worlds.redefine"),
                config.getBoolean("statistic.redefine")
        ));
        subCommands.add(new AddMemberSubCommand(
                plugin,
                "addmember",
                "am",
                "stelyclaim.claim.addmember",
                false,
                config.getStringList("worlds.addmember"),
                config.getBoolean("statistic.addmember")
        ));
        subCommands.add(new RemoveMemberSubCommand(
                plugin,
                "removemember",
                "rm",
                "stelyclaim.claim.removemember",
                false,
                config.getStringList("worlds.removemember"),
                config.getBoolean("statistic.removemember")
        ));
        subCommands.add(new AddOwnerSubCommand(
                plugin,
                "addowner",
                "ao",
                "stelyclaim.claim.addowner",
                false,
                config.getStringList("worlds.addowner"),
                config.getBoolean("statistic.addowner")
        ));
        subCommands.add(new RemoveOwnerSubCommand(
                plugin,
                "removeowner",
                "ro",
                "stelyclaim.claim.removeowner",
                false,
                config.getStringList("worlds.removeowner"),
                config.getBoolean("statistic.removeowner")
        ));
        subCommands.add(new RemoveSubCommand(
                plugin,
                "remove",
                "r",
                "stelyclaim.claim.remove",
                false,
                config.getStringList("worlds.remove"),
                config.getBoolean("statistic.remove")
        ));
        subCommands.add(new InfoSubCommand(
                plugin,
                "info",
                "i",
                "stelyclaim.claim.info",
                false,
                config.getStringList("worlds.info"),
                config.getBoolean("statistic.info")
        ));
        subCommands.add(new TeleportSubCommand(
                plugin,
                "teleport",
                "tp",
                "stelyclaim.claim.teleport",
                false,
                config.getStringList("worlds.teleport"),
                config.getBoolean("statistic.teleport")
        ));
        subCommands.add(new StatsSubCommand(
                plugin,
                "stats",
                "s",
                "stelyclaim.claim.stats",
                true,
                config.getStringList("worlds.stats"),
                config.getBoolean("statistic.stats"),
                statisticManager
        ));
        subCommands.add(new PillarSubCommand(
                plugin,
                "pillar",
                "p",
                "stelyclaim.claim.pillar",
                false,
                config.getStringList("worlds.pillar"),
                config.getBoolean("statistic.pillar"),
                helpSubCommand
        ));
    }

}
