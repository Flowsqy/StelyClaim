package fr.flowsqy.stelyclaim.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public interface EssentialsManager {

    EssentialsManager NULL = new NullEssentialsManager();

    boolean isEnable();

    User getUser(Player player);

    User getUser(UUID uuid);

    User getUser(String player);

    class NullEssentialsManager implements EssentialsManager {
        @Override
        public boolean isEnable() {
            return false;
        }

        @Override
        public User getUser(Player player) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User getUser(UUID uuid) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User getUser(String player) {
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
        public User getUser(Player player) {
            return essentials.getUser(player);
        }

        @Override
        public User getUser(UUID uuid) {
            return essentials.getUser(uuid);
        }

        @Override
        public User getUser(String player) {
            return essentials.getUser(player);
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
