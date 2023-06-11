package fr.flowsqy.stelyclaim.command.claim;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.PhysicActor;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HereSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final ConfigurationFormattedMessages messages;
    private final WorldChecker worldChecker;
    private final ProtocolManager protocolManager;
    private final PermissionData data;
    private final HelpMessage helpMessage;

    public HereSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull PermissionData data, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        messages = plugin.getMessages();
        worldChecker = new WorldChecker(worlds, messages);
        protocolManager = plugin.getProtocolManager();
        this.data = data;
        this.helpMessage = helpMessage;
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        if (worldChecker.checkCancelledWorld(context.getSender())) {
            return;
        }
        if (context.getArgsLength() != 0) {
            helpMessage.sendMessage(context, name);
            return;
        }

        final CommandSender sender = context.getSender().getBukkit();
        final PhysicActor physicActor = context.getSender().getPhysic();
        final Location senderLoc = physicActor.getLocation();
        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(physicActor.getWorld().getName()), sender);

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(
                BlockVector3.at(
                        senderLoc.getBlockX(),
                        senderLoc.getBlockY(),
                        senderLoc.getBlockZ()
                )
        );

        if (!context.hasPermission(data.getModifierPerm(context.getData(), "other"))) {
            context.getData().setStatistic(name);
            for (ProtectedRegion overlapRegion : intersecting) {
                if (!RegionFinder.isCorrectId(overlapRegion.getId())) {
                    continue;
                }
                final String[] part = overlapRegion.getId().split("_", 3);
                final ClaimHandler<?> intersectingHandler = protocolManager.getHandler(part[1]);
                if (intersectingHandler == null) {
                    continue;
                }

                if (context.getSender().isPlayer() && intersectingHandler.getOwner(part[2]).own(context.getSender().getPlayer())) {
                    messages.sendMessage(sender, "claim." + name + ".inside");
                    return;
                }
            }
            messages.sendMessage(sender, "claim." + name + ".not-inside");
            return;
        }

        final String baseMessage = messages.getFormattedMessage("claim." + name + ".message");
        final String text = messages.getFormattedMessage("claim." + name + ".text");
        final String separatorMessage = messages.getFormattedMessage("claim." + name + ".separator");

        // TODO Fix that . . . .
        if (false /*context.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.INFO))*/) {
            final String hover = messages.getFormattedMessage("claim." + name + ".hover");
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
                    final ClaimHandler<?> regionHandler = protocolManager.getHandler(parts[1]);
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
            context.getData().setStatistic(name);
            if (regions.isEmpty()) {
                messages.sendMessage(sender, "claim." + name + ".nothing");
                return;
            }
            final ComponentReplacer replacer = new ComponentReplacer(baseMessage);
            replacer.replace("%regions%", regions.toArray(new BaseComponent[0]));
            sender.spigot().sendMessage(replacer.create());
            return;
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
                final ClaimHandler<?> regionHandler = protocolManager.getHandler(parts[1]);
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
            messages.sendMessage(sender, "claim." + name + ".nothing");
            context.getData().setStatistic(name);
            return;
        }

        sender.sendMessage(baseMessage.replace("%regions%", builder.toString()));

        context.getData().setStatistic(name);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.getSender().isPhysic() && context.hasPermission(data.getBasePerm(context.getData()));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        return Collections.emptyList();
    }

}
