package fr.flowsqy.stelyclaim.protocol;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;

public class RegionManagerRetriever {

    @Nullable
    public static RegionManager retrieve(@NotNull String worldName) {
        // TODO Fix that
        return null; //return WorldGuard.getInstance().getPlatform().getRegionContainer().get(worldName));
    }

}
