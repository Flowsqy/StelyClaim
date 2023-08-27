package fr.flowsqy.stelyclaim.pillar;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RemoveSessionListener implements Listener {

    private final PillarManager pillarManager;

    public RemoveSessionListener(@NotNull PillarManager pillarManager) {
        this.pillarManager = pillarManager;
    }

    public void load(@NotNull Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent e) {
        pillarManager.removeSession(e.getPlayer().getUniqueId());
    }

}
