package fr.flowsqy.stelyclaim.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OfflinePlayerRetriever {

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(@NotNull String playerName) {
        return Bukkit.getOfflinePlayer(playerName);
    }

}
