package fr.flowsqy.stelyclaim.protocol;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RegionNameManager {

    private final static String PREFIX = "stelyclaim";
    private final static Pattern GLOBAL_PATTERN = Pattern.compile("^" + PREFIX + "_[a-z0-9]+_[A-Za-z0-9_,'\\-+/]+$");

    public static <T extends ClaimOwner> String getRegionName(@NotNull HandledOwner<T> handledOwner) {
        return PREFIX + "_" + handledOwner.handler().getId() + "_" + handledOwner.handler().getIdentifier(handledOwner.owner());
    }

    public static boolean isCorrectId(@Nullable String id) {
        return id != null && GLOBAL_PATTERN.matcher(id).matches();
    }

    /**
     * Get the parts stored in a region name
     *
     * @param regionName The region name
     * @return A String array containing the handler type and the owner identifier
     */
    @NotNull
    public static String[] getParts(@NotNull String regionName) {
        final String[] parts = regionName.split("_", 3);
        return new String[]{parts[1], parts[2]};
    }


    //TODO Remove this section


    private static FormattedMessages internalMessages;

    public static void setInternalMessages(@NotNull FormattedMessages messages) {
        if (internalMessages != null) {
            throw new IllegalStateException();
        }
        internalMessages = messages;
    }

    public static RegionManager getRegionManager(@NotNull World world, @NotNull CommandSender sender) {
        final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) {
            internalMessages.sendMessage(sender,
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
            Actor sender
    ) {
        final ProtectedRegion region = manager.getRegion(regionName);
        if (region == null) {
            internalMessages.sendMessage(sender.getBukkit(), "claim.exist.not" + (ownRegion ? "" : "-other"), "%region%", ownerName);
        }
        return region;
    }

    public static boolean mustNotExist(
            RegionManager manager,
            String regionName,
            String ownerName,
            boolean ownRegion,
            Player sender
    ) {
        final boolean exist = manager.getRegion(regionName) != null;
        if (exist) {
            internalMessages.sendMessage(sender, "claim.exist.already" + (ownRegion ? "" : "-other"), "%region%", ownerName);
        }
        return exist;
    }

}
