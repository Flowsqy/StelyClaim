package fr.flowsqy.stelyclaim.util;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class DisconnectListener implements Listener {

    private final Map<String, PillarData> pillarData;

    public DisconnectListener(StelyClaimPlugin plugin) {
        this.pillarData = plugin.getPillarData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent e) {
        pillarData.remove(e.getPlayer().getName());
    }

}
