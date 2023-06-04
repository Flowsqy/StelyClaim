package fr.flowsqy.stelyclaim.protocol;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RegionFinder {

    private final static String PREFIX = "stelyclaim";
    private final static Pattern GLOBAL_PATTERN = Pattern.compile("^" + PREFIX + "_[a-z0-9]+_[A-Za-z0-9_,'\\-+/]+$");

    public static <T extends ClaimOwner> String getRegionName(@NotNull ClaimHandler<T> handler, @NotNull T owner) {
        return PREFIX + "_" + handler.getId() + "_" + handler.getIdentifier(owner);
    }

    public static boolean isCorrectId(String id) {
        return id != null && GLOBAL_PATTERN.matcher(id).matches();
    }

    public static RegionManager getRegionManager(World world, CommandSender sender, FormattedMessages messages) {
        final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) {
            messages.sendMessage(sender,
                    "claim.world.nothandle",
                    "%world%", world.getName());
        }
        return regionManager;
    }

    public static ProtectedRegion mustExist(
            RegionManager manager,
            String regionName,
            String ownerName,
            boolean ownRegion,
            Actor sender,
            FormattedMessages messages
    ) {
        final ProtectedRegion region = manager.getRegion(regionName);
        if (region == null) {
            messages.sendMessage(sender.getBukkit(), "claim.exist.not" + (ownRegion ? "" : "-other"), "%region%", ownerName);
        }
        return region;
    }

    public static boolean mustNotExist(
            RegionManager manager,
            String regionName,
            String ownerName,
            boolean ownRegion,
            Player sender,
            FormattedMessages messages
    ) {
        final boolean exist = manager.getRegion(regionName) != null;
        if (exist) {
            messages.sendMessage(sender, "claim.exist.already" + (ownRegion ? "" : "-other"), "%region%", ownerName);
        }
        return exist;
    }

}
