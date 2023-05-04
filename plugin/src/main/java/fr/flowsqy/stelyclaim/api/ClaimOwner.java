package fr.flowsqy.stelyclaim.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public interface ClaimOwner {

    String getName();

    Set<OfflinePlayer> getMailable();

    boolean own(Player player);

}
