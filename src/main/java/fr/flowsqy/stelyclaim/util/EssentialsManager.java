package fr.flowsqy.stelyclaim.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EssentialsManager {

    EssentialsManager NULL = new NullEssentialsManager();

    boolean isEnable();

    void sendMail(UUID uuid, Supplier<String> message);

    void sendEssentialMail(OfflinePlayer from, List<OfflinePlayer> to, Function<OfflinePlayer, String> message);

    class NullEssentialsManager implements EssentialsManager {
        @Override
        public boolean isEnable() {
            return false;
        }

        @Override
        public void sendMail(UUID uuid, Supplier<String> message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendEssentialMail(OfflinePlayer from, List<OfflinePlayer> to, Function<OfflinePlayer, String> message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    class EssentialsManagerImpl implements EssentialsManager {

        private final Essentials essentials;

        public EssentialsManagerImpl(Essentials plugin) {
            Objects.requireNonNull(plugin);
            this.essentials = plugin;
        }

        @Override
        public boolean isEnable() {
            return true;
        }

        @Override
        public void sendMail(UUID uuid, Supplier<String> message) {
            final User user = essentials.getUser(uuid);
            if (user == null)
                return;
            user.addMail(message.get());
        }

        @Override
        public void sendEssentialMail(OfflinePlayer from, List<OfflinePlayer> to, Function<OfflinePlayer, String> message) {
            final User fromUser = essentials.getUser(from.getUniqueId());
            if (fromUser == null)
                return;
            final String fromName = from.getName();
            for (OfflinePlayer player : to) {
                final User user = essentials.getUser(player.getUniqueId());
                if (user == null) {
                    continue;
                }
                user.addMail(
                        I18n.tl(
                                "mailFormat",
                                fromName,
                                FormatUtil.formatMessage(
                                        fromUser,
                                        "essentials.mail",
                                        StringUtil.sanitizeString(FormatUtil.stripFormat(
                                                message.apply(player)
                                        ))
                                )
                        )
                );
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EssentialsManagerImpl that = (EssentialsManagerImpl) o;
            return essentials.equals(that.essentials);
        }

        @Override
        public int hashCode() {
            return Objects.hash(essentials);
        }

        @Override
        public String toString() {
            return "EssentialsManagerImpl{" +
                    "essentials=" + essentials +
                    '}';
        }

    }

}
