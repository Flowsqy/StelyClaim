package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpSubCommand extends SubCommand {

    private final List<SubCommand> subCommands;

    public HelpSubCommand(String name, String alias, String permission, boolean stats, boolean console, List<SubCommand> subCommands) {
        super(name, alias, permission, stats, console);
        this.subCommands = subCommands;
    }

    @Override
    public void execute(CommandSender sender, List<String> args, boolean isPlayer) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        final Stream<SubCommand> subCommandsStream;
        if (isPlayer)
            subCommandsStream = subCommands.stream()
                    .filter(subCommand -> sender.hasPermission(subCommand.getPermission()));
        else
            subCommandsStream = subCommands.stream()
                    .filter(SubCommand::isConsole);
        final int size = args.size();
        if (size != 2)
            return Collections.emptyList();
        final String arg = args.get(1).toLowerCase(Locale.ROOT);
        if(arg.equalsIgnoreCase(""))
            return subCommandsStream
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
        else
            return subCommandsStream
                    .map(SubCommand::getName)
                    .filter(name -> name.startsWith(arg))
                    .collect(Collectors.toList());
    }

}
