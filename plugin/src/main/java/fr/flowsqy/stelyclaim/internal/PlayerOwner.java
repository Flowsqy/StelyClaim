package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
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
    public boolean own(@NotNull Actor actor) {
        return actor.isPlayer() && Objects.equals(this.player.getUniqueId(), actor.getPlayer().getUniqueId());
    }

}
