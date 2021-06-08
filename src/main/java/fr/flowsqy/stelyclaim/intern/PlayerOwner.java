package fr.flowsqy.stelyclaim.intern;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerOwner implements ClaimOwner {

    private final OfflinePlayer player;

    public PlayerOwner(OfflinePlayer player) {
        Objects.requireNonNull(player);
        this.player = player;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public List<UUID> getOwners() {
        return Collections.singletonList(player.getUniqueId());
    }

    @Override
    public List<UUID> getMembers() {
        return Collections.emptyList();
    }

    @Override
    public boolean own(Player player) {
        return this.player.equals(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerOwner that = (PlayerOwner) o;
        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    @Override
    public String toString() {
        return "PlayerOwner{" +
                "player=" + player +
                '}';
    }

}
