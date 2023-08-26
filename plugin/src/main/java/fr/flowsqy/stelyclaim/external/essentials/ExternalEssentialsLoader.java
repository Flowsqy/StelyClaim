package fr.flowsqy.stelyclaim.external.essentials;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ExternalEssentialsLoader {

    public void load(@NotNull Plugin plugin) {
        final Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin == null || !essentialsPlugin.isEnabled()) {
            return;
        }
        if (!(essentialsPlugin instanceof IEssentials essentials)) {
            return;
        }
        enable(plugin, essentials);
    }

    private void enable(@NotNull Plugin plugin, @NotNull IEssentials essentials) {
        // TODO Reimplement mail feature
    }

}
