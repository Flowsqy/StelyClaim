package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class NearSubCommand extends SubCommand {

    private final ProtocolManager protocolManager;
    private final int DEFAULT_DISTANCE;
    private final int DEFAULT_MAX_DISTANCE;
    private final long COOLDOWN;
    private final int COOLDOWN_SIZE_CLEAR_CHECK;
    private final Map<UUID, Long> lastExecTimeByPlayerId;

    public NearSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin.getMessages(), name, alias, permission, console, allowedWorlds, statistic);
        final YamlConfiguration configuration = plugin.getConfiguration();
        // The distances should be >= 1 (0 is /claim here and bellow it does not make any sense)
        DEFAULT_DISTANCE = Math.max(configuration.getInt("near.default-distance", 200), 1);
        DEFAULT_MAX_DISTANCE = Math.max(configuration.getInt("near.base-max-distance", 200), 1);
        COOLDOWN = configuration.getLong("near.cooldown", 1000L);
        COOLDOWN_SIZE_CLEAR_CHECK = configuration.getInt("cooldown-size-clear-check", 4);
        lastExecTimeByPlayerId = new HashMap<>();
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        return messages.getFormattedMessage("help." + getName());
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        // Check if there is more than 'near' and a distance
        if (size > 2) {
            // Send help message
            final String helpMessage = getHelpMessage(sender);
            if (helpMessage != null) {
                sender.sendMessage(getHelpMessage(sender));
            }
            return false;
        }

        // Check full perm
        final boolean hasFullPerm = sender.hasPermission(getPermission() + "-full");

        // Init distance
        final int distance;

        // Check desired distance
        if (size == 2) {
            // Check if it's a number
            final String distanceArg = args.get(1);
            try {
                distance = Integer.parseInt(distanceArg);
            } catch (Exception ignored) {
                messages.sendMessage(sender, "util.not-a-number", "%arg%", distanceArg);
                return false;
            }

            // Check if the distance is valid (>= 1)
            if (distance < 1) {
                messages.sendMessage(sender, "claim." + getName() + ".invalid-distance", "%distance%", String.valueOf(distance));
                return false;
            }

            // Check if the player request a distance above his limit
            if (distance > DEFAULT_MAX_DISTANCE && !hasFullPerm) {
                messages.sendMessage(
                        sender,
                        "claim." + getName() + ".limit",
                        "%distance%", "%max-distance%", String.valueOf(distance), String.valueOf(DEFAULT_MAX_DISTANCE)
                );
                return false;
            }
        } else {
            // Set to default distance and limit if sender does not have full permission
            distance = hasFullPerm ? DEFAULT_DISTANCE : Math.min(DEFAULT_DISTANCE, DEFAULT_MAX_DISTANCE);
        }

        final Player player = (Player) sender;

        // Cooldown of the command
        // Check if cooldown is still active
        if (System.currentTimeMillis() - lastExecTimeByPlayerId.getOrDefault(player.getUniqueId(), 0L) < COOLDOWN) {
            messages.sendMessage(sender, getName() + ".cooldown");
            return false;
        }

        // Clear the cache of the cooldown if needed
        if (lastExecTimeByPlayerId.size() > COOLDOWN_SIZE_CLEAR_CHECK) {
            final long currentTime = System.currentTimeMillis();
            // Get expired entries
            final List<UUID> keys = lastExecTimeByPlayerId.entrySet().stream()
                    .filter(entry -> currentTime - entry.getValue() > COOLDOWN)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            // Remove them
            keys.forEach(lastExecTimeByPlayerId::remove);
        }

        // Get the region manager of the world
        final World world = player.getWorld();
        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(world.getName()), player, messages);
        if (regionManager == null) {
            return false;
        }

        final Location pos = player.getLocation();
        final int x = pos.getBlockX(), z = pos.getBlockZ();

        // Create a region with a radius of distance
        // Don't check 'y' coordinates
        final ProtectedCuboidRegion region = new ProtectedCuboidRegion("checking-area", true,
                BlockVector3.at(
                        x + distance,
                        world.getMaxHeight(),
                        z + distance
                ),
                BlockVector3.at(
                        x - distance,
                        world.getMinHeight(),
                        z - distance
                )
        );


        // Get the region in the radius
        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(region);
        final List<RegionData> detectedRegions = new LinkedList<>();
        // Remove claim where the sender is owner and format other
        for (ProtectedRegion protectedRegion : intersecting) {
            final String regionId = protectedRegion.getId();
            final String regionName;
            // Try to get the ClaimOwner if it's a StelyClaim region
            if (RegionFinder.isCorrectId(regionId)) {
                final String[] parts = regionId.split("_", 3);
                final ClaimHandler<?> regionHandler = protocolManager.getHandler(parts[1]);
                if (regionHandler == null) {
                    regionName = regionId;
                } else {
                    // Retrieve the ClaimOwner
                    final ClaimOwner claimOwner = regionHandler.getOwner(parts[2]);
                    // Check if the player own the region
                    if (claimOwner.own(player)) {
                        // Does not display the region that a player own
                        continue;
                    }
                    // Set the proper name of the region
                    regionName = claimOwner.getName();
                }
            } else {
                regionName = regionId;
            }

            // Get the nearest point
            final int[] nearestPoint = getNearestPoint(x, z, protectedRegion);
            // Get the distance between the nearest point and the player
            final int nearestX = nearestPoint[0], nearestZ = nearestPoint[1];
            final int distanceX = x - nearestX;
            final int distanceZ = z - nearestZ;
            final double distanceToNearestPoint = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
            // Register the region in the list
            detectedRegions.add(new RegionData(regionName, nearestX, nearestZ, distanceToNearestPoint));
        }

        // If there is no intersection, it means there is no region
        if (detectedRegions.isEmpty()) {
            messages.sendMessage(sender, getName() + ".no-region", "%distance%", String.valueOf(distance));
            return true;
        }

        // TODO
        // Sort it by distances

        // TODO
        // Send information
        return true;
    }


    /**
     * Get the nearest point of the region from a position
     *
     * @param posX            The x coordinate of the position
     * @param posZ            The z coordinate of the position
     * @param protectedRegion The {@link ProtectedRegion}
     * @return An {@code int} array of two elements, the x and z coordinate of the nearest point
     */
    private int[] getNearestPoint(int posX, int posZ, ProtectedRegion protectedRegion) {
        final int[] nearestPoint = new int[2];
        // TODO Better check for polygon region
        // This check is exact only for cuboid
        final BlockVector3 max = protectedRegion.getMaximumPoint();
        final BlockVector3 min = protectedRegion.getMinimumPoint();
        nearestPoint[0] = getNearestPointOnLine(posX, min.getBlockX(), max.getBlockX());
        nearestPoint[1] = getNearestPointOnLine(posZ, min.getBlockZ(), max.getBlockZ());
        return nearestPoint;
    }

    /**
     * Get the nearest point on a line from another point
     *
     * @param p   The coordinate of the point
     * @param min The minimum coordinate of the segment
     * @param max The maximum coordinate of the segment
     * @return The coordinate of the nearest point
     */
    private int getNearestPointOnLine(int p, int min, int max) {
        // p is too far in positive way
        if (p > max) {
            return max;
        }
        // p is too far in negative way
        if (p < min) {
            return min;
        }
        // p is
        return p;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return Collections.emptyList();
    }

    private final static class RegionData {

        private final String name;
        private final int nearestX, nearestZ;
        private final double distance;

        public RegionData(String name, int nearestX, int nearestZ, double distance) {
            this.name = name;
            this.nearestX = nearestX;
            this.nearestZ = nearestZ;
            this.distance = distance;
        }
    }

}
