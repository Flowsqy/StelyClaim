package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public record PlayerOwner(@NotNull OfflinePlayer player) implements ClaimOwner {

    public PlayerOwner {
        Objects.requireNonNull(player);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Set<OfflinePlayer> getMailable() {
        return Collections.singleton(player);
    }

    @Override
    public boolean own(Player player) {
        return this.player.equals(player);
    }

}
