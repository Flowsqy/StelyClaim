package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HelpSubCommand extends SubCommand {

    private final ClaimCommand claimCommand;

    public HelpSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, ClaimCommand claimCommand) {
        super(plugin.getMessages(), name, alias, permission, console, allowedWorlds, statistic);
        this.claimCommand = claimCommand;
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return messages.getFormattedMessage("help." + getName());
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if (size > 2) {
            // Wrong call
            return messages.sendMessage(sender, "help.help");
        }
        List<SubCommand> availableSubCommands = claimCommand.getAvailableSubCommand(sender, isPlayer).collect(Collectors.toList());
        // Check for specific help
        if (size == 2) {
            final String secondArg = args.get(1).toLowerCase(Locale.ROOT);
            if (!secondArg.isEmpty()) {
                // Get all matching sub commands
                final List<SubCommand> matchingSubCommands = availableSubCommands.stream()
                        .filter(subCmd -> subCmd.getName().equals(secondArg) || subCmd.getAlias().equals(secondArg))
                        .collect(Collectors.toList());
                // Limit to the matching sub-commands only if there is some
                if (!matchingSubCommands.isEmpty()) {
                    availableSubCommands = matchingSubCommands;
                }
            }
        }
        availableSubCommands.forEach(subCommand -> {
                    final String helpMessage = subCommand.getHelpMessage(sender);
                    if (helpMessage != null) {
                        sender.sendMessage(helpMessage);
                    }
                }
        );
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        // Tab all SubCommands
        final int size = args.size();
        if (size != 2) {
            return Collections.emptyList();
        }

        return claimCommand.getAvailableSubCommandNameCompletion(sender, args.get(1).toLowerCase(Locale.ROOT), isPlayer);
    }

}
