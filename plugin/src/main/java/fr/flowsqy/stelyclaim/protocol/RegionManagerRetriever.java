package fr.flowsqy.stelyclaim.protocol;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import fr.flowsqy.stelyclaim.util.WorldName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegionManagerRetriever {

    @Nullable
    public static RegionManager retrieve(@NotNull String worldName) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(new WorldName(worldName));
    }

}
