package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    private final ClaimCommand claimCommand;
    private final BedrockCommand bedrockCommand;

    public CommandManager(StelyClaimPlugin plugin) {

        final PluginCommand claimCmd = plugin.getCommand("claim");
        final PluginCommand bedrockCmd = plugin.getCommand("bedrock");

        assert claimCmd != null;
        assert bedrockCmd != null;

        final String configNoPerm = plugin.getMessages().getMessage("util.noperm");

        claimCmd.setPermissionMessage(configNoPerm);
        bedrockCmd.setPermissionMessage(configNoPerm);

        claimCommand = new ClaimCommand(plugin);
        claimCmd.setExecutor(claimCommand);
        claimCmd.setTabCompleter(claimCommand);

        bedrockCommand = new BedrockCommand(plugin);
        bedrockCmd.setExecutor(bedrockCommand);
        bedrockCmd.setTabCompleter(bedrockCommand);

    }

    public ClaimCommand getClaimCommand() {
        return claimCommand;
    }

    public BedrockCommand getBedrockCommand() {
        return bedrockCommand;
    }
}
