package fr.flowsqy.stelyclaim.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EssentialsManagerImpl extends EssentialsManager {

    private final Essentials essentials;
    private final Messages messages;

    public EssentialsManagerImpl(Plugin plugin, Messages messages) {
        this.essentials = (Essentials) plugin;
        this.messages = messages;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    public Essentials getEssentials() {
        return essentials;
    }

    public void sendMail(User fromUser, Player fromPlayer, User to, String command, boolean useEssentialsSyntax, String target) {
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

}
