package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.io.BedrockManager;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BedrockCommand implements TabExecutor {

    private final Messages messages;
    private final BedrockManager manager;

    public BedrockCommand(Messages messages, BedrockManager manager) {
        this.messages = messages;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return messages.sendMessage(sender, "util.onlyplayer");
        }

        final Player player = (Player) sender;

        System.out.println(messages.getMessage("bedrock.on"));
        System.out.println(messages.getMessage("bedrock.off"));

        if(manager.toggle(player.getName(), true))
            return messages.sendMessage(player, "bedrock.enable");

        return messages.sendMessage(player, "bedrock.disable");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
