package fr.flowsqy.stelyclaim.api;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface ClaimOwner {

    String getName();

    Set<OfflinePlayer> getMailable();

    boolean own(@NotNull Actor actor);

}
