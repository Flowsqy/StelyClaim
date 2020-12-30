package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    public CommandManager(StelyClaimPlugin plugin, Messages messages){

        final PluginCommand claimCmd = plugin.getCommand("claim");
        final PluginCommand bedrockCmd = plugin.getCommand("bedrock");

        assert claimCmd != null;
        assert bedrockCmd != null;

        final String configNoPerm = messages.getMessage("util.noperm");

        claimCmd.setPermissionMessage(configNoPerm);
        bedrockCmd.setPermissionMessage(configNoPerm);



    }

}
