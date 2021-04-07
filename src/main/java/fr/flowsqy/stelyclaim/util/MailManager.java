package fr.flowsqy.stelyclaim.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MailManager {

    private final Messages messages;
    private final Map<String, Boolean> commands;
    private final boolean enabled;
    private final Supplier<Essentials> getEssentials;
    private final SendMailFunction sendMailFunction;

    public MailManager(Messages messages, Configuration config, EssentialsManager essentialsManager) {
        this.messages = messages;
        this.enabled = essentialsManager.isEnable();
        if (enabled) {
            commands = new HashMap<>();
            initCustomFormat(config);
            final EssentialsManagerImpl essentialsManagerImpl = (EssentialsManagerImpl) essentialsManager;
            getEssentials = essentialsManagerImpl::getEssentials;
            sendMailFunction = essentialsManagerImpl::sendMail;
            return;
        }
        commands = null;
        getEssentials = null;
        sendMailFunction = null;
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
        final User userTo = getEssentials.get().getUser(to);
        if (userTo == null)
            return;
        if (customFormat) {
            sendMailFunction.sendMail(null, from, userTo, command, false, target);
            return;
        }
        final User userFrom = getEssentials.get().getUser(from);
        if (userFrom == null)
            return;
        sendMailFunction.sendMail(userFrom, from, userTo, command, true, target);
    }

    public void sendMail(Player from, Player to, String command, String target) {
        if (!enabled)
            return;
        final Boolean customFormat = commands.get(command);
        if (customFormat == null) // Command does not enable mail
            return;
        final User userTo = getEssentials.get().getUser(to);
        if (userTo == null)
            return;
        if (customFormat) {
            sendMailFunction.sendMail(null, from, userTo, command, false, target);
            return;
        }
        final User userFrom = getEssentials.get().getUser(from);
        if (userFrom == null)
            return;
        sendMailFunction.sendMail(userFrom, from, userTo, command, true, target);
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

    @FunctionalInterface
    private interface SendMailFunction {

        void sendMail(User fromUser, Player fromPlayer, User to, String command, boolean useEssentialsSyntax, String target);

    }

}
