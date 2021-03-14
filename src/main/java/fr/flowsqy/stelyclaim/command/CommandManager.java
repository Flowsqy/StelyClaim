package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    public CommandManager(StelyClaimPlugin plugin) {

        final PluginCommand claimCmd = plugin.getCommand("claim");
        final PluginCommand bedrockCmd = plugin.getCommand("bedrock");

        assert claimCmd != null;
        assert bedrockCmd != null;

        final String configNoPerm = plugin.getMessages().getMessage("util.noperm");

        claimCmd.setPermissionMessage(configNoPerm);
        bedrockCmd.setPermissionMessage(configNoPerm);

        final ClaimCommand claimExecutor = new ClaimCommand(plugin);
        claimCmd.setExecutor(claimExecutor);
        claimCmd.setTabCompleter(claimExecutor);

        final BedrockCommand bedrockExecutor = new BedrockCommand(plugin);
        bedrockCmd.setExecutor(bedrockExecutor);
        bedrockCmd.setTabCompleter(bedrockExecutor);

    }

}
