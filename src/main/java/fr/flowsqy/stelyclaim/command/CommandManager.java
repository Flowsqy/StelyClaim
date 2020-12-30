package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    public CommandManager(StelyClaimPlugin plugin, Messages messages){

        final PluginCommand claimCmd = plugin.getCommand("claim");
        final PluginCommand bedrockCmd = plugin.getCommand("bedrock");

        assert claimCmd != null;
        assert bedrockCmd != null;

        final String noPermMessage = ChatColor.RED + "You don't have the permission to do this command";
        final String configNoPerm = messages.getMessage("util.noperm", noPermMessage);

        claimCmd.setPermissionMessage(configNoPerm);
        bedrockCmd.setPermissionMessage(configNoPerm);



    }

}
