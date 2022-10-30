package fr.flowsqy.stelyclaim.util;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.*;

public class MailManager {

    private final ConfigurationFormattedMessages messages;
    private final Map<String, Boolean> commands;
    private final EssentialsManager essentialsManager;

    public MailManager(ConfigurationFormattedMessages messages, Configuration config, EssentialsManager essentialsManager) {
        this.messages = messages;
        this.essentialsManager = essentialsManager;
        if (essentialsManager.isEnable()) {
            commands = new HashMap<>();
            initCustomFormat(config);
            return;
        }
        commands = null;
    }

    private void initCustomFormat(Configuration config) {
        // SubCommands that can send mails : define, redefine, addmember, removemember, addowner, removeowner, remove
        for (String command : Arrays.asList("define", "redefine", "addmember", "removemember", "addowner", "removeowner", "remove")) {
            if (config.getBoolean("mail." + command + ".enabled"))
                commands.put(command, config.getBoolean("mail." + command + ".custom-format"));
        }
    }

    public boolean isEnabled() {
        return essentialsManager.isEnable();
    }

    private void sendMail(Player from, List<OfflinePlayer> to, String command, OfflinePlayer target) {
        if (!isEnabled())
            return;
        final Boolean customFormat = commands.get(command);
        if (customFormat == null) // Command does not enable mail
            return;
        final String mailMessage;
        if (target != null) {
            mailMessage = messages.getFormattedMessage(
                    "mail." + command,
                    "%from%", "%target%",
                    from.getName(), target.getName()
            );
        } else {
            mailMessage = messages.getFormattedMessage(
                    "mail." + command,
                    "%from%",
                    from.getName()
            );
        }

        if (customFormat) {
            for (OfflinePlayer player : to) {
                essentialsManager.sendMail(player.getUniqueId(), () -> mailMessage.replace("%to%", String.valueOf(player.getName())));
            }
        } else {
            essentialsManager.sendEssentialMail(from, to, player -> mailMessage.replace("%to%", String.valueOf(player.getName())));
        }
    }

    public void sendInfoToOwner(Player sender, ClaimOwner owner, FormattedMessages messages, String command) {
        sendInfoToOwner(sender, owner, messages, command, null);
    }

    public void sendInfoToOwner(Player sender, ClaimOwner owner, FormattedMessages messages, String command, OfflinePlayer target) {
        final Set<OfflinePlayer> mailablePlayers = owner.getMailable();
        final List<Player> connectedPlayers = new ArrayList<>();
        final List<OfflinePlayer> disconnectedPlayers = new ArrayList<>();
        for (OfflinePlayer player : mailablePlayers) {
            if (player == null)
                continue;
            final Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null) {
                connectedPlayers.add(onlinePlayer);
            } else {
                disconnectedPlayers.add(player);
            }
        }
        if (!connectedPlayers.isEmpty()) {
            final String[] replaces;
            if (target == null) {
                replaces = new String[]{"%sender%", sender.getName()};
            } else {
                replaces = new String[]{"%sender%", "%target%", sender.getName(), target.getName()};
            }
            final String message = messages.getFormattedMessage("claim.target." + command, replaces);
            for (Player player : connectedPlayers) {
                player.sendMessage(message);
            }
        }
        if (!disconnectedPlayers.isEmpty()) {
            sendMail(
                    sender,
                    disconnectedPlayers,
                    command,
                    target
            );
        }
    }

}
