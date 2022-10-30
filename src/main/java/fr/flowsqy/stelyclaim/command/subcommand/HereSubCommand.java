package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HereSubCommand extends SubCommand {

    public HereSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if (size != 1) {
            messages.sendMessage(sender,
                    "help."
                            + getName()
                            + (sender.hasPermission(getPermission() + "-other") ? "-other" : "")
            );
            return false;
        }
        final PlayerHandler handler = plugin.getProtocolManager().getHandler("player");

        final Player player = (Player) sender;
        final Location playerLoc = player.getLocation();
        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(player.getWorld().getName()), player, handler.getMessages());

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(
                BlockVector3.at(
                        playerLoc.getBlockX(),
                        playerLoc.getBlockY(),
                        playerLoc.getBlockZ()
                )
        );

        if (!player.hasPermission(getOtherPermission())) {
            for (ProtectedRegion overlapRegion : intersecting) {
                if (!RegionFinder.isCorrectId(overlapRegion.getId())) {
                    continue;
                }
                final String[] part = overlapRegion.getId().split("_", 3);
                final ClaimHandler<?> intersectingHandler = plugin.getProtocolManager().getHandler(part[1]);
                if (intersectingHandler == null) {
                    continue;
                }

                if (intersectingHandler.getOwner(part[2]).own(player)) {
                    messages.sendMessage(player, "claim.here.inside");
                    return true;
                }
            }
            messages.sendMessage(player, "claim.here.not-inside");
            return true;
        }

        final String baseMessage = messages.getFormattedMessage("claim.here.message");
        final String text = messages.getFormattedMessage("claim.here.text");
        final String separatorMessage = messages.getFormattedMessage("claim.here.separator");

        if (player.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.INFO))) {
            final String hover = messages.getFormattedMessage("claim.here.hover");
            final List<BaseComponent> separator = new ArrayList<>(
                    Arrays.asList(
                            TextComponent.fromLegacyText(
                                    separatorMessage
                            )
                    )
            );
            final List<BaseComponent> regions = new ArrayList<>();
            boolean first = true;
            for (ProtectedRegion overlapRegion : intersecting) {
                if (first)
                    first = false;
                else {
                    regions.addAll(separator);
                }
                final String regionId = overlapRegion.getId();
                final String regionName;
                boolean playerClaim;
                if (RegionFinder.isCorrectId(regionId)) {
                    final String[] parts = regionId.split("_", 3);
                    final ClaimHandler<?> regionHandler = plugin.getProtocolManager().getHandler(parts[1]);
                    if (regionHandler == null) {
                        regionName = regionId;
                        playerClaim = false;
                    } else {
                        regionName = regionHandler.getOwner(parts[2]).getName();
                        playerClaim = regionHandler instanceof PlayerHandler;
                    }
                } else {
                    regionName = regionId;
                    playerClaim = false;
                }
                final TextComponent component = new TextComponent(
                        TextComponent.fromLegacyText(
                                text.replace("%region%", regionName)
                        )
                );
                if (hover != null) {
                    component.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new Text(
                                            hover.replace("%region%", regionName)
                                    )
                            )
                    );
                }

                if (playerClaim) {
                    component.setClickEvent(
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/claim info " + regionName
                            )
                    );
                }
                regions.add(component);
            }
            if (regions.isEmpty()) {
                messages.sendMessage(player, "claim.here.nothing");
                return true;
            }
            final ComponentReplacer replacer = new ComponentReplacer(baseMessage);
            replacer.replace("%regions%", regions.toArray(new BaseComponent[0]));
            player.spigot().sendMessage(replacer.create());
            return true;
        }

        final StringBuilder builder = new StringBuilder();
        for (ProtectedRegion overlapRegion : intersecting) {
            if (builder.length() > 0) {
                builder.append(separatorMessage);
            }
            final String regionId = overlapRegion.getId();
            final String regionName;
            if (RegionFinder.isCorrectId(regionId)) {
                final String[] parts = regionId.split("_", 3);
                final ClaimHandler<?> regionHandler = plugin.getProtocolManager().getHandler(parts[1]);
                if (regionHandler == null) {
                    regionName = regionId;
                } else {
                    regionName = regionHandler.getOwner(parts[2]).getName();
                }
            } else {
                regionName = regionId;
            }
            builder.append(text.replace("%region%", regionName));
        }

        if (builder.length() == 0) {
            messages.sendMessage(player, "claim.here.nothing");
            return true;
        }

        player.sendMessage(baseMessage.replace("%regions%", builder.toString()));

        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return Collections.emptyList();
    }

}
