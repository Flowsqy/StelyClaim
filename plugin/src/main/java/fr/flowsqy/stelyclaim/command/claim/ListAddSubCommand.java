package fr.flowsqy.stelyclaim.command.claim;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.HandlerRegistry;
import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.protocol.RegionHandler;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ListAddSubCommand implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final ConfigurationFormattedMessages messages;
    private final WorldChecker worldChecker;
    private final HandlerRegistry handlerRegistry;
    private final OtherPermissionChecker permChecker;
    private final HelpMessage helpMessage;
    private final long CACHE_PERIOD;
    private final int CACHE_SIZE_CLEAR_CHECK;
    private final int REGION_BY_PAGE;

    private final Map<CacheKey, CacheData> cache;

    private final String regionMessage;

    public ListAddSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                             @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
                             @NotNull OtherPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        messages = plugin.getMessages();
        worldChecker = new WorldChecker(worlds, messages);
        handlerRegistry = plugin.getHandlerRegistry();
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
        final Configuration configuration = plugin.getConfiguration();
        CACHE_PERIOD = configuration.getLong(name + ".cache-period", 4000);
        CACHE_SIZE_CLEAR_CHECK = configuration.getInt(name + ".cache-size-clear-checkFull", 4);
        REGION_BY_PAGE = Math.max(configuration.getInt(name + ".region-by-page", 5), 1);
        cache = new HashMap<>();
        regionMessage = messages.getFormattedMessage("claim." + name + ".region-message");
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    private TextComponent getTextComponent(String category, int page, String player) {
        final TextComponent component = new TextComponent();
        final String componentText = messages.getFormattedMessage("claim." + name + "." + category + "-text");
        final BaseComponent[] text = TextComponent.fromLegacyText(componentText);
        for (BaseComponent c : text) {
            component.addExtra(c);
        }
        final String hoverText = messages.getFormattedMessage("claim." + name + "." + category + "-hover");
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim " + name + " " + player + page));
        return component;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (worldChecker.checkCancelledWorld(context.getActor())) {
            return;
        }
        if (regionMessage == null) {
            // TODO Stats stuff
            // context.getData().setStatistic(name);
            return;
        }

        final boolean hasOtherPerm = permChecker.checkOther(context);
        final OfflinePlayer target;
        String pageArg = null;
        int page = 1;
        if (context.getArgsLength() == 0) {
            if (!context.getActor().isPlayer()) {
                helpMessage.sendMessage(context, id);
                return;
            }
            target = context.getActor().getPlayer();
        } else if (context.getArgsLength() == 1) {
            if (hasOtherPerm) {
                target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
            } else {
                target = context.getActor().getPlayer();
                pageArg = context.getArg(0);
            }
        } else if (context.getArgsLength() == 3 && hasOtherPerm) {
            target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
            pageArg = context.getArg(1);
        } else {
            helpMessage.sendMessage(context, id);
            return;
        }
        final CommandSender sender = context.getActor().getBukkit();
        final boolean targetedIsSender = context.getActor().isPlayer() &&
                context.getActor().getPlayer().getUniqueId().equals(target.getUniqueId());
        if (pageArg != null) {
            try {
                page = Integer.parseInt(pageArg);
            } catch (NumberFormatException e) {
                messages.sendMessage(sender, "util.not-a-number", "%arg%", pageArg);
                return;
            }
            if (page < 1) {
                messages.sendMessage(sender, "claim." + name + ".invalid-page", "%page%", String.valueOf(page));
                return;
            }
        }

        final World world = context.getActor().getPhysic().getWorld();
        final UUID targetUUID = target.getUniqueId();
        final CacheKey cacheKey = new CacheKey(targetUUID, world.getName());

        CacheData cacheData = cache.get(cacheKey);

        // Get from cache or register
        if (cacheData == null || System.currentTimeMillis() - cacheData.initialInput > CACHE_PERIOD) {
            final RegionManager manager = RegionNameManager.getRegionManager(new WorldName(world.getName()), sender);
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
                    "claim." + name + ".no-region" + (targetedIsSender ? "" : "-other"),
                    "%player%",
                    targetName);
            return;
        }

        final int modulo = result.size() % REGION_BY_PAGE;
        final int pageCount = (result.size() - modulo) / REGION_BY_PAGE + (modulo > 0 ? 1 : 0);
        // Wrong page number
        if (page > pageCount) {
            messages.sendMessage(
                    sender,
                    "claim." + name + ".not-enough-page",
                    "%page%", "%arg%",
                    String.valueOf(pageCount), String.valueOf(page));
            return;
        }

        // Send region list
        for (int index = (page - 1) * REGION_BY_PAGE, i = 0; index < result.size()
                && i < REGION_BY_PAGE; index++, i++) {
            final String regionId = result.get(index);
            final RegionHandler regionHandler = new RegionHandler(regionId);
            final String regionName = regionHandler.getName(handlerRegistry);
            sender.sendMessage(regionMessage.replace("%region%", regionName));
        }

        // Send page navigation message
        String pageMessage = messages.getFormattedMessage("claim." + name + ".page-message");
        if (pageMessage != null) {
            pageMessage = pageMessage.replace("%page%", String.valueOf(page));
            if (page == 1) {
                pageMessage = pageMessage.replace(
                        "%previous%",
                        messages.getFormattedMessage("claim." + name + ".no-previous"));
                if (page == pageCount) {
                    // No previous and no next
                    pageMessage = pageMessage.replace(
                            "%next%",
                            messages.getFormattedMessage("claim." + name + ".no-next"));
                    sender.sendMessage(pageMessage);
                    // context.getData().setStatistic(name);
                    return;
                }
                // No previous but next
                final TextComponent nextComponent = getTextComponent(
                        "next",
                        page + 1,
                        hasOtherPerm ? (targetName + " ") : "");
                /*
                final ComponentReplacer replacer = new ComponentReplacer(pageMessage);
                sender.spigot().sendMessage(
                        replacer.replace(
                                "%next%", new BaseComponent[]{nextComponent}).create());*/
                // TODO Stats stuff
                // context.getData().setStatistic(name);
            } else if (page == pageCount) {
                // Previous but no next
                pageMessage = pageMessage.replace(
                        "%next%",
                        messages.getFormattedMessage("claim." + name + ".no-next"));
                final TextComponent previousComponent = getTextComponent(
                        "previous",
                        page - 1,
                        hasOtherPerm ? (targetName + " ") : "");
                /*
                final ComponentReplacer replacer = new ComponentReplacer(pageMessage);
                sender.spigot().sendMessage(
                        replacer.replace(
                                "%previous%", new BaseComponent[]{previousComponent}).create());*/
                // TODO Stats stuff
                // context.getData().setStatistic(name);
            } else {
                // Previous and next
                //final ComponentReplacer replacer = new ComponentReplacer(pageMessage);
                final TextComponent previousComponent = getTextComponent(
                        "previous",
                        page - 1,
                        hasOtherPerm ? (targetName + " ") : "");
                final TextComponent nextComponent = getTextComponent(
                        "next",
                        page + 1,
                        hasOtherPerm ? (targetName + " ") : "");
                /*
                sender.spigot().sendMessage(
                        replacer
                                .replace(
                                        "%previous%", new BaseComponent[]{previousComponent})
                                .replace(
                                        "%next%", new BaseComponent[]{nextComponent})
                                .create());*/
            }
        }
        // TODO Stats stuff

        // context.getData().setStatistic(name);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getActor().isPhysic() && permChecker.checkOther(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        if (context.getArgsLength() != 1 || !permChecker.checkOther(context)) {
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
