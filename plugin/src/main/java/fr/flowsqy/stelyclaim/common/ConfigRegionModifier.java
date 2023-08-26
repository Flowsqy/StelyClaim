package fr.flowsqy.stelyclaim.common;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import fr.flowsqy.stelyclaim.pillar.CuboidPillarCoordinate;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class ConfigRegionModifier<T extends ClaimOwner> implements RegionModifier<T> {

    private final Configuration config;
    private final FormattedMessages messages;
    private final String category;
    private final Function<T, UUID> targetSupplier;

    public ConfigRegionModifier(Configuration config, FormattedMessages messages, String category, Function<T, UUID> targetSupplier) {
        this.config = config;
        this.messages = messages;
        this.category = category;
        this.targetSupplier = targetSupplier;
    }

    @SuppressWarnings("deprecation") // WorldGuard warnings, can't be avoided
    @Override
    public void modify(Player sender, ProtectedRegion region, T claimOwner) {

        // Teleportation flag
        final String setTp = config.getString(category + ".set-tp");
        if (setTp != null) {
            final CuboidPillarCoordinate pillarCoordinate = new CuboidPillarCoordinate(region, sender.getWorld());

            final Location location = switch (setTp) {
                case "here" -> sender.getLocation();
                case "northwest" -> pillarCoordinate.getNorthWestLocation();
                case "northeast" -> pillarCoordinate.getNorthEastLocation();
                case "southwest" -> pillarCoordinate.getSouthWestLocation();
                case "southeast" -> pillarCoordinate.getSouthEastLocation();
                default -> null;
            };

            if (location != null) {
                final Vector tpModifier = new Vector(config.getDouble(category + ".tp-modifier.x", 0d), config.getDouble(category + ".tp-modifier.y", 0d), config.getDouble(category + ".tp-modifier.z", 0d));
                region.setFlag(Flags.TELE_LOC, BukkitAdapter.adapt(location.add(tpModifier)));
            }
        }


        // Owners
        final List<String> owners = config.getStringList(category + ".owner");
        final List<String> ownerGroups = config.getStringList(category + ".owner-group");
        if (!owners.isEmpty() || !ownerGroups.isEmpty()) {
            final DefaultDomain ownerDomain = region.getOwners();
            for (String owner : owners) {
                if (owner.equals("%sender")) {
                    ownerDomain.addPlayer(sender.getUniqueId());
                } else if (owner.equals("%target%")) {
                    final UUID targetId = targetSupplier.apply(claimOwner);
                    if (targetId != null) {
                        ownerDomain.addPlayer(targetId);
                    }
                } else {
                    try {
                        ownerDomain.addPlayer(UUID.fromString(owner));
                    } catch (IllegalArgumentException e) {
                        ownerDomain.addPlayer(owner);
                    }
                }
            }
            for (String groupOwner : ownerGroups)
                ownerDomain.addGroup(groupOwner);
        }

        // Members
        final List<String> members = config.getStringList(category + ".member");
        final List<String> memberGroups = config.getStringList(category + ".member-group");
        if (!members.isEmpty() || !memberGroups.isEmpty()) {
            final DefaultDomain memberDomain = region.getMembers();
            for (String member : members) {
                if (member.equals("%sender")) {
                    memberDomain.addPlayer(sender.getUniqueId());
                } else if (member.equals("%target%")) {
                    final UUID targetId = targetSupplier.apply(claimOwner);
                    if (targetId != null) {
                        memberDomain.addPlayer(targetId);
                    }
                } else {
                    try {
                        memberDomain.addPlayer(UUID.fromString(member));
                    } catch (IllegalArgumentException e) {
                        memberDomain.addPlayer(member);
                    }
                }
            }
            for (String memberOwner : memberGroups)
                memberDomain.addGroup(memberOwner);
        }

        // Greeting flag
        final String greeting = messages.getFormattedMessage("claim.selection-flags." + category + ".greeting", "%region%", claimOwner.getName());
        region.setFlag(Flags.GREET_MESSAGE, greeting);

        // Farewell flag
        final String farewell = messages.getFormattedMessage("claim.selection-flags." + category + ".farewell", "%region%", claimOwner.getName());
        region.setFlag(Flags.FAREWELL_MESSAGE, farewell);

        // States flags
        final ConfigurationSection section = config.getConfigurationSection(category + ".flags");
        if (section != null) {
            final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (!(entry.getValue() instanceof Boolean)) continue;
                final Flag<?> flag = registry.get(entry.getKey());
                if (!(flag instanceof StateFlag stateFlag)) continue;
                final boolean value = (boolean) entry.getValue();
                if (value && region.getFlag(stateFlag) == StateFlag.State.ALLOW) continue;
                if ((stateFlag.getDefault() == StateFlag.State.ALLOW && value) || (stateFlag.getDefault() == null && !value)) {
                    region.setFlag(stateFlag, null);
                } else {
                    region.setFlag(stateFlag, value ? StateFlag.State.ALLOW : StateFlag.State.DENY);
                }
            }
        }
    }
}
