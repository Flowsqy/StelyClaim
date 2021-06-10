package fr.flowsqy.stelyclaim.api;

import org.bukkit.command.CommandSender;

public interface ClaimMessage {

    void sendMessage(CommandSender sender, String path, String... replace);

}
