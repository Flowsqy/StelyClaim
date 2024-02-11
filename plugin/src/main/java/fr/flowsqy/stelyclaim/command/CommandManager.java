package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.PluginCommand;

import java.util.Objects;

public class CommandManager {

    /*
    private final ClaimCommand claimCommand;
    private final BedrockCommand bedrockCommand;
    */

    public CommandManager(StelyClaimPlugin plugin) {

        /*
        final PluginCommand claimCmd = Objects.requireNonNull(plugin.getCommand("claim"));
        final PluginCommand bedrockCmd = Objects.requireNonNull(plugin.getCommand("bedrock"));

        final String configNoPerm = plugin.getMessages().getFormattedMessage("util.noperm");

        claimCmd.setPermissionMessage(configNoPerm);
        bedrockCmd.setPermissionMessage(configNoPerm);

        claimCommand = new ClaimCommand(plugin, Objects.requireNonNull(claimCmd.getPermission()));
        claimCmd.setExecutor(claimCommand);
        claimCmd.setTabCompleter(claimCommand);

        bedrockCommand = new BedrockCommand(plugin);
        bedrockCmd.setExecutor(bedrockCommand);
        bedrockCmd.setTabCompleter(bedrockCommand);
        */

    }

    /*
    @SuppressWarnings("unused") // API
    public ClaimCommand getClaimCommand() {
        return claimCommand;
    }

    @SuppressWarnings("unused") // API
    public BedrockCommand getBedrockCommand() {
        return bedrockCommand;
    }
    */

}
