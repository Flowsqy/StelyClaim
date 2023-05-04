package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class OtherSubCommand extends SubCommand {

    public OtherSubCommand(ConfigurationFormattedMessages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(messages, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        final String other = sender.hasPermission(ClaimCommand.Permissions.getOtherPerm(getPermission())) ? "-other" : "";
        return messages.getFormattedMessage("help." + getName() + other);
    }

    public String getOtherPermission() {
        return ClaimCommand.Permissions.getOtherPerm(permission);
    }

}
