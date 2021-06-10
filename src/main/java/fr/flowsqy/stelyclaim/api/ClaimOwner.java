package fr.flowsqy.stelyclaim.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface ClaimOwner {

    String getName();

    List<OfflinePlayer> getMailable();

    boolean own(Player player);

}
