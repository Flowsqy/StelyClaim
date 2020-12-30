package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.command.subcommand.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimCommand implements TabExecutor {

    private final List<SubCommand> subCommands;
    private final HelpSubCommand helpSubCommand;

    public ClaimCommand(){
        subCommands = new ArrayList<>();

        initCommands();
        this.helpSubCommand = new HelpSubCommand(
                "help",
                "h",
                "stelyclaim.claim.help",
                false,
                true,
                subCommands
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final String arg = (args == null || args.length < 1 ? "" : args[0]).toLowerCase(Locale.ROOT);
        final Optional<SubCommand> subCommand = getSubCommand(arg);
        if(subCommand.isPresent()) {
            if (sender.hasPermission(helpSubCommand.getName()))
                return subCommand.get().tab(
                        sender,
                        args == null ?
                                new ArrayList<>() :
                                new ArrayList<>(Arrays.asList(args))
                );
            else
                return Collections.emptyList();
        }
        else {
            final Stream<SubCommand> subCommandStream;
            if(sender instanceof Player)
                subCommandStream = subCommands.stream()
                        .filter(cmd -> sender.hasPermission(cmd.getPermission()));
            else
                subCommandStream = subCommands.stream()
                        .filter(SubCommand::isConsole);
            return subCommandStream
                    .map(SubCommand::getName)
                    .filter(cmd -> cmd.startsWith(arg))
                    .collect(Collectors.toList());
        }
    }

    private Optional<SubCommand> getSubCommand(String arg){
        if(arg == null || arg.equals("") || arg.equalsIgnoreCase("help"))
            return Optional.of(helpSubCommand);
        return subCommands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(arg) || cmd.getAlias().equalsIgnoreCase(arg))
                .findAny();
    }

    private void initCommands() {
        subCommands.add(new DefineSubCommand(
                "define",
                "d",
                "stelyclaim.claim.define",
                true,
                false
        ));
        subCommands.add(new RedefineSubCommand(
                "redefine",
                "rd",
                "stelyclaim.claim.redefine",
                true,
                false
        ));
        subCommands.add(new AddMemberSubCommand(
                "addmember",
                "am",
                "stelyclaim.claim.addmember",
                true,
                true
        ));
        subCommands.add(new RemoveMemberSubCommand(
                "removemember",
                "rm",
                "selyclaim.claim.removemember",
                true,
                true
        ));
        subCommands.add(new AddOwnerSubCommand(
                "addowner",
                "ao",
                "stelyclaim.claim.addowner",
                true,
                true
        ));
        subCommands.add(new RemoveOwnerSubCommand(
                "removeowner",
                "ro",
                "stelyclaim.claim.removeowner",
                true,
                true
        ));
        subCommands.add(new RemoveSubCommand(
                "remove",
                "r",
                "stelyclaim.claim.remove",
                true,
                true
        ));
        subCommands.add(new InfoSubCommand(
                "info",
                "i",
                "stelyclaim.claim.info",
                false,
                true
        ));
        subCommands.add(new TeleportSubCommand(
                "teleport",
                "tp",
                "stelyclaim.claim.teleport",
                false,
                false
        ));
        subCommands.add(new StatsSubCommand(
                "stats",
                "s",
                "stelyclaim.claim.stats",
                false,
                true
        ));
    }

}
