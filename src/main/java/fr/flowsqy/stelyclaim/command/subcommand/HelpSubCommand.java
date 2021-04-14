package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpSubCommand extends SubCommand {

    private final List<SubCommand> subCommands;
    private final Supplier<Integer> getTabLimitSupplier;

    public HelpSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, List<SubCommand> subCommands, Supplier<Integer> getTabLimitSupplier) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        this.subCommands = subCommands;
        this.getTabLimitSupplier = getTabLimitSupplier;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final int argsSize = args.size();
        if (argsSize > 2) {
            // Wrong call
            final String firstArg = args.get(0);
            if (firstArg.equalsIgnoreCase(getName()) || firstArg.equalsIgnoreCase(getAlias())) {
                // Check if help was called to display only help's help
                messages.sendMessage(sender, "help.help");
                return true;
            }
        } else if (argsSize == 2) {
            final String firstArg = args.get(0).toLowerCase(Locale.ROOT);
            final String secondArg = args.get(1).toLowerCase(Locale.ROOT);

            if (
                    (firstArg.equalsIgnoreCase(getName()) || firstArg.equalsIgnoreCase(getAlias()))
                            && !secondArg.isEmpty()
            ) {
                // Want help of the second argument
                final Stream<SubCommand> subCommandStream;
                if (sender instanceof Player)
                    subCommandStream = subCommands.stream()
                            .filter(cmd -> sender.hasPermission(cmd.getPermission()));
                else
                    subCommandStream = subCommands.stream()
                            .filter(SubCommand::isConsole);
                // Get the optional SubCommand
                final Optional<SubCommand> optionalSubCommand = subCommandStream
                        .limit(getTabLimitSupplier.get()) // Exclude non tab commands
                        .filter(cmd -> cmd.getName().equalsIgnoreCase(secondArg) || cmd.getAlias().equalsIgnoreCase(secondArg))
                        .findAny();
                if (optionalSubCommand.isPresent()) {
                    // Display the help
                    final SubCommand subCommand = optionalSubCommand.get();
                    final String other =
                            !(subCommand instanceof HelpSubCommand) &&
                                    sender.hasPermission(subCommand.getPermission() + "-other") ?
                                    "-other" : "";
                    subCommand.messages.sendMessage(sender, "help." + subCommand.getName() + other);
                    return true;
                }
            }
        }
        // Display all help
        messages.sendMessage(sender, "help.help");
        if (sender instanceof Player)
            subCommands.stream()
                    .limit(getTabLimitSupplier.get()) // Exclude non tab commands
                    .skip(1)
                    .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                    .forEach(cmd -> {
                                final boolean other = sender.hasPermission(cmd.getPermission() + "-other");
                                cmd.messages.sendMessage(sender, "help." + cmd.getName() + (other ? "-other" : ""));
                            }
                    );
        else
            subCommands.stream()
                    .limit(getTabLimitSupplier.get()) // Exclude non tab commands
                    .skip(1)
                    .filter(SubCommand::isConsole)
                    .forEach(cmd -> cmd.messages.sendMessage(sender, "help." + cmd.getName() + "-other"));
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        // Tab all SubCommands
        final int size = args.size();
        if (size != 2)
            return Collections.emptyList();

        final Stream<SubCommand> subCommandsStream;
        if (isPlayer)
            subCommandsStream = subCommands.stream()
                    .limit(getTabLimitSupplier.get()) // Exclude non tab commands
                    .filter(subCommand -> sender.hasPermission(subCommand.getPermission()));
        else
            subCommandsStream = subCommands.stream()
                    .limit(getTabLimitSupplier.get()) // Exclude non tab commands
                    .filter(SubCommand::isConsole);
        final String arg = args.get(1).toLowerCase(Locale.ROOT);
        if (arg.isEmpty())
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
