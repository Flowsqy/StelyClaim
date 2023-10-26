package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PlayerOwner(@NotNull OfflinePlayer player) implements ClaimOwner {

    public PlayerOwner {
        Objects.requireNonNull(player);
    }

    @Override
    public @NotNull String getName() {
        return Objects.toString(player.getName());
    }

    @Override
    public boolean own(@NotNull Actor actor) {
        return actor.isPlayer() && Objects.equals(this.player.getUniqueId(), actor.getPlayer().getUniqueId());
    }

}
