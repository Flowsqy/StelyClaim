package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ListAddSubCommand extends RegionSubCommand {

    private final long CACHE_PERIOD;
    private final int CACHE_SIZE_CLEAR_CHECK;
    private final int REGION_BY_PAGE;

    private final Map<CacheKey, CacheData> cache;

    private final String regionMessage;

    public ListAddSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        final Configuration configuration = plugin.getConfiguration();
        CACHE_PERIOD = configuration.getLong(getName() + ".cache-period", 4000);
        CACHE_SIZE_CLEAR_CHECK = configuration.getInt(getName() + "cache-size-clear-check", 4);
        REGION_BY_PAGE = Math.max(configuration.getInt(getName() + "region-by-page", 5), 1);
        cache = new HashMap<>();
        regionMessage = messages.getMessage("claim." + getName() + ".region-message");
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;

        if (regionMessage == null)
            return true;

        final boolean hasOtherPerm = player.hasPermission(getPermission() + "-other");
        final String targetName;
        final boolean own;
        String pageArg = null;
        int page = 1;
        if (size == 1) {
            targetName = player.getName();
            own = true;
        } else if (size == 2) {
            if (hasOtherPerm) {
                targetName = args.get(1);
                own = player.getName().equals(targetName);
            } else {
                targetName = player.getName();
                own = true;
                pageArg = args.get(1);
            }
        } else if (size == 3 && hasOtherPerm) {
            targetName = args.get(1);
            own = player.getName().equals(targetName);
            pageArg = args.get(2);
        } else {
            return messages.sendMessage(player, "help." + getName() + (hasOtherPerm ? "-other" : ""));
        }
        if (pageArg != null) {
            try {
                page = Integer.parseInt(pageArg);
            } catch (NumberFormatException e) {
                return messages.sendMessage(player, "claim." + getName() + ".not-a-number", "%arg%", pageArg);
            }
            if (page < 1) {
                return messages.sendMessage(player, "claim." + getName() + ".invalid-page", "%page%", String.valueOf(page));
            }
        }

        final World world = player.getWorld();
        final CacheKey cacheKey = new CacheKey(targetName, world.getName());

        CacheData cacheData = cache.get(cacheKey);

        // Get from cache or register
        if (cacheData == null || System.currentTimeMillis() - cacheData.initialInput > CACHE_PERIOD) {
            final RegionManager manager = getRegionManager(world);
            final List<String> result = manager.getRegions().entrySet().stream()
                    .filter(entry -> {
                        final ProtectedRegion region = entry.getValue();
                        return region.getMembers().contains(targetName) || region.getOwners().contains(targetName);
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
                    .collect(Collectors.toList());
            for (CacheKey key : keys)
                cache.remove(key);
        }


        final List<String> result = cacheData.result;

        // No regions
        if (result.isEmpty()) {
            return messages.sendMessage(
                    player,
                    "claim." + getName() + ".no-region" + (own ? "" : "-other"),
                    "%player%",
                    targetName
            );
        }

        final int modulo = result.size() % REGION_BY_PAGE;
        final int pageCount = (result.size() - modulo) / REGION_BY_PAGE + (modulo > 0 ? 1 : 0);
        // Wrong page number
        if (page > pageCount) {
            return messages.sendMessage(
                    player,
                    "claim." + getName() + ".not-enough-page",
                    "%page%", "%arg%",
                    String.valueOf(pageCount), String.valueOf(page)
            );
        }

        // Send region list
        for (int index = (page - 1) * REGION_BY_PAGE, i = 0; index < result.size() && i < REGION_BY_PAGE; index++, i++) {
            player.sendMessage(regionMessage.replace("%region%", result.get(index)));
        }

        // Send page navigation message
        final String pageMessage = messages.getMessage("claim." + getName() + ".page-message");
        if (pageMessage != null) {
            String finalMessage = pageMessage.replace("%page%", String.valueOf(page));
            if (page == 1) {
                finalMessage = finalMessage.replace(
                        "%previous%",
                        messages.getMessage("claim." + getName() + ".no-previous")
                );
                if (page == pageCount) {
                    // No previous and no next
                    finalMessage = finalMessage.replace(
                            "%next%",
                            messages.getMessage("claim." + getName() + ".no-next")
                    );
                    player.sendMessage(finalMessage);
                    return true;
                }
                // No previous but next
                final TextComponent nextComponent = getTextComponent(
                        "next",
                        page + 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                final ComponentReplacer replacer = new ComponentReplacer(finalMessage);
                player.spigot().sendMessage(
                        replacer.replace(
                                "%next%", new BaseComponent[]{nextComponent}
                        ).create()
                );
                return true;
            } else if (page == pageCount) {
                // Previous but no next
                finalMessage = finalMessage.replace(
                        "%next%",
                        messages.getMessage("claim." + getName() + ".no-next")
                );
                final TextComponent previousComponent = getTextComponent(
                        "previous",
                        page - 1,
                        hasOtherPerm ? (targetName + " ") : ""
                );
                final ComponentReplacer replacer = new ComponentReplacer(finalMessage);
                player.spigot().sendMessage(
                        replacer.replace(
                                "%previous%", new BaseComponent[]{previousComponent}
                        ).create()
                );
                return true;
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
                player.spigot().sendMessage(
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
        return true;
    }

    private TextComponent getTextComponent(String category, int page, String player) {
        final TextComponent component = new TextComponent();
        component.setExtra(
                Arrays.asList(
                        TextComponent.fromLegacyText(
                                messages.getMessage("claim." + getName() + "." + category + "-text")
                        )
                )
        );
        component.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(
                                messages.getMessage("claim." + getName() + "." + category + "-hover")
                        )
                )
        );
        component.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/claim " + getName() + " " + player + page
                )
        );
        return component;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (args.size() == 2 && sender.hasPermission(getPermission() + "-other")) {
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            final Player player = (Player) sender;
            if (arg.isEmpty())
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player::canSee)
                        .map(HumanEntity::getName)
                        .collect(Collectors.toList());
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private final static class CacheKey {

        private final String playerName;
        private final String world;

        public CacheKey(String playerName, String world) {
            this.playerName = playerName;
            this.world = world;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return playerName.equals(cacheKey.playerName) && world.equals(cacheKey.world);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerName, world);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "playerName='" + playerName + '\'' +
                    ", world='" + world + '\'' +
                    '}';
        }
    }

    private final static class CacheData {

        private final long initialInput;
        private final List<String> result;

        public CacheData(long initialInput, List<String> result) {
            this.initialInput = initialInput;
            this.result = result;
        }

    }

}
