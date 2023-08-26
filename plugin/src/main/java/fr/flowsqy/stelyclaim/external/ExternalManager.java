package fr.flowsqy.stelyclaim.external;

import fr.flowsqy.stelyclaim.external.essentials.ExternalEssentialsLoader;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ExternalManager {

    public void load(@NotNull Plugin plugin) {
        final ExternalEssentialsLoader externalEssentialsLoader = new ExternalEssentialsLoader();
        externalEssentialsLoader.load(plugin);
    }

}
