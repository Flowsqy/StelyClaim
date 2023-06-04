package fr.flowsqy.stelyclaim.command.claim;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import fr.flowsqy.stelyclaim.util.WorldName;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ListAddSubCommand implements CommandNode {

    private final static String NAME = "listadd";
    private final ConfigurationFormattedMessages messages;
    private final ProtocolManager protocolManager;
    private final long CACHE_PERIOD;
    private final int CACHE_SIZE_CLEAR_CHECK;
    private final int REGION_BY_PAGE;

    private final Map<CacheKey, CacheData> cache;

    private final String regionMessage;

    public ListAddSubCommand(@NotNull StelyClaimPlugin plugin) {
        messages = plugin.getMessages();
        protocolManager = plugin.getProtocolManager();
        final Configuration configuration = plugin.getConfiguration();
        CACHE_PERIOD = configuration.getLong(NAME + ".cache-period", 4000);
        CACHE_SIZE_CLEAR_CHECK = configuration.getInt(NAME + ".cache-size-clear-check", 4);
        REGION_BY_PAGE = Math.max(configuration.getInt(NAME + ".region-by-page", 5), 1);
        cache = new HashMap<>();
        regionMessage = messages.getFormattedMessage("claim." + NAME + ".region-message");
    }

    private TextComponent getTextComponent(String category, int page, String player) {
        final TextComponent component = new TextComponent();
        component.setExtra(
                Arrays.asList(
                        TextComponent.fromLegacyText(
                                messages.getFormattedMessage("claim." + NAME + "." + category + "-text")
                        )
                )
        );
        component.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(
                                messages.getFormattedMessage("claim." + NAME + "." + category + "-hover")
                        )
                )
        );
        component.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/claim " + NAME + " " + player + page
                )
        );
        return component;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (regionMessage == null)
            return; // TODO Update stats ?

        final boolean hasOtherPerm = context.hasPermission(getOtherPermission());
        final OfflinePlayer target;
        String pageArg = null;
        int page = 1;
        if (context.getArgsLength() == 0) {
            if (!context.getSender().isPlayer()) {
                new HelpMessage().sendMessage(context); // TODO Specify listadd
                return;
            }
            target = context.getSender().getPlayer();
        } else if (context.getArgsLength() == 1) {
            if (hasOtherPerm) {
                target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
            } else {
                target = context.getSender().getPlayer();
                pageArg = context.getArg(0);
            }
        } else if (context.getArgsLength() == 3 && hasOtherPerm) {
            target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
            pageArg = context.getArg(1);
        } else {
            new HelpMessage().sendMessage(context); // TODO Specify listadd
            return;
        }
        final CommandSender sender = context.getSender().getBukkit();
        final boolean targetedIsSender = context.getSender().isPlayer() &&
                context.getSender().getPlayer().getUniqueId().equals(target.getUniqueId());
        if (pageArg != null) {
            try {
                page = Integer.parseInt(pageArg);
            } catch (NumberFormatException e) {
                messages.sendMessage(sender, "util.not-a-number", "%arg%", pageArg);
                return;
            }
            if (page < 1) {
                messages.sendMessage(sender, "claim." + NAME + ".invalid-page", "%page%", String.valueOf(page));
                return;
            }
        }

        final World world = context.getSender().getPhysic().getWorld();
        final UUID targetUUID = target.getUniqueId();
        final CacheKey cacheKey = new CacheKey(targetUUID, world.getName());

        CacheData cacheData = cache.get(cacheKey);

        // Get from cache or register
        if (cacheData == null || System.currentTimeMillis() - cacheData.initialInput > CACHE_PERIOD) {
            final RegionManager manager = RegionFinder.getRegionManager(new WorldName(world.getName()), sender, messages);
            if (manager == null)
                return;
            final List<String> result = manager.getRegions().entrySet().stream()
                    .filter(entry -> {
                        final ProtectedRegion region = entry.getValue();
                        return region.getMembers().contains(targetUUID) || region.getOwners().contains(targetUUID);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            cacheData = new CacheData(System.currentTimeMillis(), result);
            cache.put(cacheKey, cacheData);
        }

        // Cache clear
        if (cache.size() > CACHE_SIZE_CLEAR_CHECK) {
            final List<CacheKey> keys = cache.entrySet().stream()
                    .filter(entry -> System.currentTimeMillis() - entry.getValue().initialInput > CACHE_PERIOD)
                    .map(Map.Entry::getKey)
                    .toList();
            for (CacheKey key : keys)
                cache.remove(key);
        }


        final String targetName = target.getName();
        final List<String> result = cacheData.result;

        // No regions
        if (result.isEmpty()) {
            messages.sendMessage(
                    sender,
                    "claim." + NAME + ".no-region" + (targetedIsSender ? "" : "-other"),
                    "%player%",
                    targetName
            );
            return;
        }

        final int modulo = result.size() % REGION_BY_PAGE;
        final int pageCount = (result.size() - modulo) / REGION_BY_PAGE + (modulo > 0 ? 1 : 0);
        // Wrong page number
        if (page > pageCount) {
            messages.sendMessage(
                    sender,
                    "claim." + NAME + ".not-enough-page",
                    "%page%", "%arg%",
                    String.valueOf(pageCount), String.valueOf(page)
            );
            return;
        }

        // Send region list
        for (int index = (page - 1) * REGION_BY_PAGE, i = 0; index < result.size() && i < REGION_BY_PAGE; index++, i++) {
            final String regionId = result.get(index);
            final String regionName;
            if (RegionFinder.isCorrectId(regionId)) {
                final String[] parts = regionId.split("_", 3);
                final ClaimHandler<?> regionHandler = protocolManager.getHandler(parts[1]);
                if (regionHandler == null) {
                    regionName = regionId;
                } else {
                    regionName = regionHandler.getOwner(parts[2]).getName();
                }
            } else {
                regionName = regionId;
            }
            sender.sendMessage(regionMessage.replace("%region%", regionName));
        }

        // Send page navigation message
        final String pageMessage = messages.getFormattedMessage("claim." + NAME + ".page-message");
        if (pageMessage != null) {
            String finalMessage = pageMessage.replace("%page%", String.valueOf(page));
            if (page == 1) {
                finalMessage = finalMessage.replace(
                        "%previous%",
                        messages.getFormattedMessage("claim." + NAME + ".no-previous")
                );
                if (page == pageCount) {
                    // No previous and no next
                    finalMessage = finalMessage.replace(
                            "%next%",
                            messages.getFormattedMessage("claim." + NAME + ".no-next")
                    );
                    sender.sendMessage(finalMessage);
                    // TODO Update stats
                    return;
                }
                // No previous but next
                final TextComponent nextComponent = getTextComponent(
                        "next",
                        page + 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                final ComponentReplacer replacer = new ComponentReplacer(finalMessage);
                sender.spigot().sendMessage(
                        replacer.replace(
                                "%next%", new BaseComponent[]{nextComponent}
                        ).create()
                );
                // TODO Update stats
                return;
            } else if (page == pageCount) {
                // Previous but no next
                finalMessage = finalMessage.replace(
                        "%next%",
                        messages.getFormattedMessage("claim." + NAME + ".no-next")
                );
                final TextComponent previousComponent = getTextComponent(
                        "previous",
                        page - 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                final ComponentReplacer replacer = new ComponentReplacer(finalMessage);
                sender.spigot().sendMessage(
                        replacer.replace(
                                "%previous%", new BaseComponent[]{previousComponent}
                        ).create()
                );
                // TODO Update stats
                return;
            } else {
                // Previous and next
                final ComponentReplacer replacer = new ComponentReplacer(finalMessage);
                final TextComponent previousComponent = getTextComponent(
                        "previous",
                        page - 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                final TextComponent nextComponent = getTextComponent(
                        "next",
                        page + 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                sender.spigot().sendMessage(
                        replacer
                                .replace(
                                        "%previous%", new BaseComponent[]{previousComponent}
                                )
                                .replace(
                                        "%next%", new BaseComponent[]{nextComponent}
                                ).create()
                );
            }
        }
        // TODO Update stats
    }

    @Override
    public @NotNull String[] getTriggers() {
        return new String[]{NAME, "la"};
    }

    @Override
    public @NotNull String getTabCompletion() {
        return NAME;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getSender().isPhysic() && context.hasPermission(getCommandPerm);
    }

    @Override
    public boolean canTabComplete(@NotNull CommandContext context) {
        return canExecute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        if (context.getArgsLength() != 1 || !context.hasPermission(getOtherPerm)) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ROOT);
        if (arg.isEmpty())
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                .collect(Collectors.toList());
    }

    private record CacheKey(@NotNull UUID playerName, @NotNull String world) {
    }

    private record CacheData(long initialInput, @NotNull List<String> result) {
    }

}
