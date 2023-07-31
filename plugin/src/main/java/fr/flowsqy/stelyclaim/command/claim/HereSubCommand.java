package fr.flowsqy.stelyclaim.command.claim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.HandlerRegistry;
import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.actor.PhysicActor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import fr.flowsqy.stelyclaim.util.WorldName;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class HereSubCommand implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final ConfigurationFormattedMessages messages;
    private final WorldChecker worldChecker;
    private final HandlerRegistry handlerRegistry;
    private final OtherCommandPermissionChecker permChecker;
    private final HelpMessage helpMessage;

    public HereSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherCommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        messages = plugin.getMessages();
        worldChecker = new WorldChecker(worlds, messages);
        handlerRegistry = plugin.getHandlerRegistry();
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        final Actor actor = context.getActor();
        if (worldChecker.checkCancelledWorld(actor)) {
            return;
        }
        if (context.getArgsLength() != 0) {
            helpMessage.sendMessage(context, id);
            return;
        }

        final CommandSender sender = actor.getBukkit();
        final PhysicActor physicActor = actor.getPhysic();
        final Location senderLoc = physicActor.getLocation();
        final RegionManager regionManager = RegionNameManager
                .getRegionManager(new WorldName(physicActor.getWorld().getName()), sender);

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(
                BlockVector3.at(
                        senderLoc.getBlockX(),
                        senderLoc.getBlockY(),
                        senderLoc.getBlockZ()));

        if (!permChecker.checkOther(context)) {
            // TODO Stats stuff
            // context.getData().setStatistic(name);
            for (ProtectedRegion overlapRegion : intersecting) {
                if (!RegionNameManager.isCorrectId(overlapRegion.getId())) {
                    continue;
                }
                final String[] part = overlapRegion.getId().split("_", 3);
                final ClaimHandler<?> intersectingHandler = handlerRegistry.getHandler(part[1]);
                if (intersectingHandler == null) {
                    continue;
                }

                if (context.getActor().isPlayer()
                        && intersectingHandler.getOwner(part[2]).owner().own(context.getActor())) {
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
        if (false /*
                   * contextual.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.
                   * Permissions.INFO))
                   */) {
            final String hover = messages.getFormattedMessage("claim." + name + ".hover");
            final List<BaseComponent> separator = new ArrayList<>(
                    Arrays.asList(
                            TextComponent.fromLegacyText(
                                    separatorMessage)));
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
                if (RegionNameManager.isCorrectId(regionId)) {
                    final String[] parts = regionId.split("_", 3);
                    final ClaimHandler<?> regionHandler = handlerRegistry.getHandler(parts[1]);
                    if (regionHandler == null) {
                        regionName = regionId;
                        playerClaim = false;
                    } else {
                        regionName = regionHandler.getOwner(parts[2]).owner().getName();
                        playerClaim = regionHandler instanceof PlayerHandler;
                    }
                } else {
                    regionName = regionId;
                    playerClaim = false;
                }
                final TextComponent component = new TextComponent(
                        TextComponent.fromLegacyText(
                                text.replace("%region%", regionName)));
                if (hover != null) {
                    component.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new Text(
                                            hover.replace("%region%", regionName))));
                }

                if (playerClaim) {
                    component.setClickEvent(
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/claim info " + regionName));
                }
                regions.add(component);
            }
            // TODO Stats Stuff
            // context.getData().setStatistic(name);
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
            if (RegionNameManager.isCorrectId(regionId)) {
                final String[] parts = regionId.split("_", 3);
                final ClaimHandler<?> regionHandler = handlerRegistry.getHandler(parts[1]);
                if (regionHandler == null) {
                    regionName = regionId;
                } else {
                    regionName = regionHandler.getOwner(parts[2]).owner().getName();
                }
            } else {
                regionName = regionId;
            }
            builder.append(text.replace("%region%", regionName));
        }

        if (builder.length() == 0) {
            messages.sendMessage(sender, "claim." + name + ".nothing");
            // TODO Stats stuff
            // context.getData().setStatistic(name);
            return;
        }

        sender.sendMessage(baseMessage.replace("%regions%", builder.toString()));
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
        return context.getActor().isPhysic() && permChecker.checkBase(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        return Collections.emptyList();
    }

}
