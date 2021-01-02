package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.*;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimCommand implements TabExecutor {

    private final Messages messages;
    private final List<SubCommand> subCommands;
    private final SubCommand helpSubCommand;

    public ClaimCommand(StelyClaimPlugin plugin){
        this.messages = plugin.getMessages();
        subCommands = new ArrayList<>();
        initCommands(plugin);
        helpSubCommand = subCommands.get(0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final List<String> argsList = args == null ?
                new ArrayList<>() :
                new ArrayList<>(Arrays.asList(args));
        final String arg = argsList.size() < 1 ? "" : argsList.get(0).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> subCommand = getSubCommand(arg);
        final boolean isPlayer = sender instanceof Player;
        if(subCommand.isPresent()) {
            final SubCommand subCmd = subCommand.get();
            if (isPlayer ? sender.hasPermission(subCmd.getPermission()) : subCmd.isConsole()){
                subCmd.execute(sender, argsList, argsList.size(), isPlayer);
                return true;
            }
            if(!isPlayer)
                return messages.sendMessage(sender, "util.onlyplayer");
        }
        if(sender.hasPermission(helpSubCommand.getPermission()))
            helpSubCommand.execute(sender, argsList, argsList.size(), isPlayer);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> argsList = args == null ?
                new ArrayList<>() :
                new ArrayList<>(Arrays.asList(args));
        final String arg = argsList.size() < 1 ? "" : argsList.get(0).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> subCommand = getSubCommand(arg);
        if(subCommand.isPresent()) {
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
            final Stream<SubCommand> subCommandStream;
            if(sender instanceof Player)
                subCommandStream = subCommands.stream()
                        .filter(cmd -> sender.hasPermission(cmd.getPermission()));
            else {
                subCommandStream = subCommands.stream()
                        .filter(SubCommand::isConsole);
            }
            if(arg.equalsIgnoreCase(""))
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
            return Collections.emptyList();
    }

    private Optional<SubCommand> getSubCommand(String arg){
        if (arg.equals(""))
            return Optional.empty();
        return subCommands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(arg) || cmd.getAlias().equalsIgnoreCase(arg))
                .findAny();
    }

    private void initCommands(StelyClaimPlugin plugin) {
        subCommands.add(new HelpSubCommand(
                plugin,
                "help",
                "h",
                "stelyclaim.claim.help",
                false,
                true,
                subCommands
        ));
        subCommands.add(new DefineSubCommand(
                plugin,
                "define",
                "d",
                "stelyclaim.claim.define",
                true,
                false
        ));
        subCommands.add(new RedefineSubCommand(
                plugin,
                "redefine",
                "rd",
                "stelyclaim.claim.redefine",
                true,
                false
        ));
        subCommands.add(new AddMemberSubCommand(
                plugin,
                "addmember",
                "am",
                "stelyclaim.claim.addmember",
                true,
                false
        ));
        subCommands.add(new RemoveMemberSubCommand(
                plugin,
                "removemember",
                "rm",
                "stelyclaim.claim.removemember",
                true,
                false
        ));
        subCommands.add(new AddOwnerSubCommand(
                plugin,
                "addowner",
                "ao",
                "stelyclaim.claim.addowner",
                true,
                false
        ));
        subCommands.add(new RemoveOwnerSubCommand(
                plugin,
                "removeowner",
                "ro",
                "stelyclaim.claim.removeowner",
                true,
                false
        ));
        subCommands.add(new RemoveSubCommand(
                plugin,
                "remove",
                "r",
                "stelyclaim.claim.remove",
                true,
                false
        ));
        subCommands.add(new InfoSubCommand(
                plugin,
                "info",
                "i",
                "stelyclaim.claim.info",
                false,
                false
        ));
        subCommands.add(new TeleportSubCommand(
                plugin,
                "teleport",
                "tp",
                "stelyclaim.claim.teleport",
                false,
                false
        ));
        subCommands.add(new StatsSubCommand(
                plugin,
                "stats",
                "s",
                "stelyclaim.claim.stats",
                false,
                true
        ));
    }

}
