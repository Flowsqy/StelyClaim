package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class HereSubCommand extends RegionSubCommand {

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
        final Player player = (Player) sender;
        final Location playerLoc = player.getLocation();
        final RegionManager regionManager = getRegionManager(player.getWorld());

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(
                BlockVector3.at(
                        playerLoc.getBlockX(),
                        playerLoc.getBlockY(),
                        playerLoc.getBlockZ()
                )
        );

        final String playerNameLower = player.getName().toLowerCase(Locale.ROOT);

        if (!player.hasPermission(getPermission() + "-other")) {
            for (ProtectedRegion overlapRegion : intersecting) {
                if (overlapRegion.getId().equals(playerNameLower)) {
                    messages.sendMessage(player, "claim.here.inside");
                    return true;
                }
            }
            messages.sendMessage(player, "claim.here.not-inside");
            return true;
        }

        final String baseMessage = messages.getMessage("claim.here.message");
        final String text = messages.getMessage("claim.here.text");
        final String separatorMessage = messages.getMessage("claim.here.separator");

        if (player.hasPermission("stelyclaim.claim.info-other")) {
            final String hover = messages.getMessage("claim.here.hover");
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
                final TextComponent component = new TextComponent(
                        TextComponent.fromLegacyText(
                                text.replace("%region%", regionId)
                        )
                );
                if (hover != null) {
                    component.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new Text(
                                            hover.replace("%region%", regionId)
                                    )
                            )
                    );
                }
                component.setClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/claim info " + regionId
                        )
                );
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
            builder.append(text.replace("%region%", overlapRegion.getId()));
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
