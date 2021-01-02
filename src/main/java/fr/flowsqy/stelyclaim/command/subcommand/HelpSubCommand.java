package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpSubCommand extends SubCommand {

    private final List<SubCommand> subCommands;

    public HelpSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console, List<SubCommand> subCommands) {
        super(plugin, name, alias, permission, stats, console);
        this.subCommands = subCommands;
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final int argsSize = args.size();
        final boolean help;
        if(argsSize > 2){
            final String firstArg = args.get(0);
            if(firstArg.equalsIgnoreCase(getName()) || firstArg.equalsIgnoreCase(getAlias())){
                messages.sendMessage(sender, "help.help");
                return;
            }
        }
        else if (argsSize == 2){
            final String firstArg = args.get(0).toLowerCase(Locale.ROOT);
            final String secondArg = args.get(1).toLowerCase(Locale.ROOT);

            if(
                    (firstArg.equalsIgnoreCase(getName()) || firstArg.equalsIgnoreCase(getAlias()))
                    && !secondArg.equalsIgnoreCase("")
            ){
                final Stream<SubCommand> subCommandStream;
                if(sender instanceof Player)
                    subCommandStream = subCommands.stream()
                            .filter(cmd -> sender.hasPermission(cmd.getPermission()));
                else {
                    subCommandStream = subCommands.stream()
                            .filter(SubCommand::isConsole);
                }
                final Optional<SubCommand> optionalSubCommand = subCommandStream
                        .filter(cmd -> cmd.getName().equalsIgnoreCase(secondArg) || cmd.getAlias().equalsIgnoreCase(secondArg))
                        .findAny();
                if(optionalSubCommand.isPresent()) {
                    final SubCommand subCommand = optionalSubCommand.get();
                    final String other =
                            !(subCommand instanceof HelpSubCommand) &&
                            sender.hasPermission(subCommand.getPermission()+"-other") ?
                                    "-other" : "";
                    messages.sendMessage(sender, "help." + subCommand.getName() + other);
                    return;
                }
            }
        }
        messages.sendMessage(sender, "help.help");
        if(sender instanceof Player)
            subCommands.stream()
                    .skip(1)
                    .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                    .forEach(cmd -> {
                                boolean other = sender.hasPermission(cmd.getPermission()+"-other");
                                messages.sendMessage(sender, "help."+cmd.getName()+(other ? "-other" : ""));
                            }
                    );
        else
            subCommands.stream()
                    .skip(1)
                    .filter(SubCommand::isConsole)
                    .forEach(cmd -> messages.sendMessage(sender, "help."+cmd.getName()+"-other"));

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
