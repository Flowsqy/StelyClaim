package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;

import java.util.List;

public abstract class ProtocolSubCommand extends OtherSubCommand {

    protected final ProtocolManager protocolManager;

    public ProtocolSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        this(plugin.getProtocolManager(), plugin.getMessages(), name, alias, permission, console, allowedWorlds, statistic);
    }

    public ProtocolSubCommand(ProtocolManager protocolManager, ConfigurationFormattedMessages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(messages, name, alias, permission, console, allowedWorlds, statistic);
        this.protocolManager = protocolManager;
    }
}
