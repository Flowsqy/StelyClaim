package fr.flowsqy.stelyclaim.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MailManager {

    private final Messages messages;
    private final Essentials essentials;
    private final boolean enabled;
    private final Map<String, Boolean> commands;

    public MailManager(Messages messages, Configuration config) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (!(plugin instanceof Essentials)) {
            this.messages = null;
            essentials = null;
            enabled = false;
            commands = null;
            return;
        }
        this.messages = messages;
        essentials = (Essentials) plugin;
        enabled = true;
        commands = new HashMap<>();
        initCustomFormat(config);
    }

    private void initCustomFormat(Configuration config) {
        // SubCommands that can send mails : define, redefine, addmember, removemember, addowner, removeowner, remove
        for (String command : Arrays.asList("define", "redefine", "addmember", "removemember", "addowner", "removeowner", "remove")) {
            if (config.getBoolean("mail." + command + ".enabled"))
                commands.put(command, config.getBoolean("mail." + command + ".custom-format"));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void sendMail(Player from, String to, String command) {
        sendMail(from, to, command, null);
    }

    public void sendMail(Player from, Player to, String command) {
        sendMail(from, to, command, null);
    }

    public void sendMail(Player from, String to, String command, String target) {
        if (!enabled)
            return;
        final Boolean customFormat = commands.get(command);
        if (customFormat == null) // Command does not enable mail
            return;
        final User userTo = essentials.getUser(to);
        if (userTo == null)
            return;
        if (customFormat) {
            sendMail(null, from, userTo, command, false, target);
            return;
        }
        final User userFrom = essentials.getUser(from);
        if (userFrom == null)
            return;
        sendMail(userFrom, from, userTo, command, true, target);
    }

    public void sendMail(Player from, Player to, String command, String target) {
        if (!enabled)
            return;
        final Boolean customFormat = commands.get(command);
        if (customFormat == null) // Command does not enable mail
            return;
        final User userTo = essentials.getUser(to);
        if (userTo == null)
            return;
        if (customFormat) {
            sendMail(null, from, userTo, command, false, target);
            return;
        }
        final User userFrom = essentials.getUser(from);
        if (userFrom == null)
            return;
        sendMail(userFrom, from, userTo, command, true, target);
    }

    private void sendMail(User fromUser, Player fromPlayer, User to, String command, boolean useEssentialsSyntax, String target) {
        final String mailMessage;
        if (target != null) {
            mailMessage = messages.getMessage(
                    "mail." + command,
                    "%from%", "%to%", "%target%",
                    fromPlayer.getName(), to.getName(), target
            );
        } else {
            mailMessage = messages.getMessage(
                    "mail." + command,
                    "%from%", "%to%",
                    fromPlayer.getName(), to.getName()
            );
        }

        if (useEssentialsSyntax) {
            to.addMail(
                    I18n.tl(
                            "mailFormat",
                            fromUser.getName(),
                            FormatUtil.formatMessage(
                                    fromUser,
                                    "essentials.mail",
                                    StringUtil.sanitizeString(FormatUtil.stripFormat(mailMessage))
                            )
                    )
            );
        } else {
            to.addMail(mailMessage);
        }
    }

    public void sendInfoToTarget(Player sender, String target, String command) {
        final Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer != null && sender.canSee(targetPlayer)) {
            messages.sendMessage(targetPlayer, "claim.target." + command, "%sender%", sender.getName());
        } else {
            sendMail(
                    sender,
                    targetPlayer == null ? target : targetPlayer.getName(),
                    command
            );
        }
    }

    public void sendInfoToTarget(Player sender, String target, String command, String argTarget) {
        final Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer != null && sender.canSee(targetPlayer)) {
            messages.sendMessage(
                    targetPlayer,
                    "claim.target." + command,
                    "%sender%", "%target%",
                    sender.getName(), argTarget);
        } else {
            sendMail(
                    sender,
                    targetPlayer == null ? target : targetPlayer.getName(),
                    command,
                    argTarget
            );
        }
    }

}
