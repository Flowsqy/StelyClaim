package fr.flowsqy.stelyclaim.command.claim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
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
import fr.flowsqy.stelyclaim.protocol.RegionHandler;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import fr.flowsqy.stelyclaim.util.WorldName;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
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
    private final OtherCommandPermissionChecker infoPermChecker;
    private final HelpMessage helpMessage;

    public HereSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherCommandPermissionChecker permChecker, @NotNull OtherCommandPermissionChecker infoPermChecker,
            @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        messages = plugin.getMessages();
        worldChecker = new WorldChecker(worlds, messages);
        handlerRegistry = plugin.getHandlerRegistry();
        this.permChecker = permChecker;
        this.infoPermChecker = infoPermChecker;
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

        /*
        if (!permChecker.checkOther(context)) {
            // TODO Stats stuff
            // context.getData().setStatistic(name);
            for (ProtectedRegion overlapRegion : intersecting) {
                final RegionHandler regionHandler = new RegionHandler(overlapRegion.getId());
                if (!regionHandler.isInternalRegion()) {
                    continue;
                }
                final HandledOwner<?> handledOwner = regionHandler.getOwner(handlerRegistry);
                if (handledOwner == null) {
                    continue;
                }

                if (handledOwner.owner().own(context.getActor())) {
                    messages.sendMessage(sender, "claim." + name + ".inside");
                    return;
                }
            }
            messages.sendMessage(sender, "claim." + name + ".not-inside");
            return;
        }*/

        final boolean hasOtherPerm = permChecker.checkOther(context);

        final String rawBaseMessage = messages.getFormattedMessage("claim." + name + ".message");
        final String rawRegionTextTemplate = messages.getFormattedMessage("claim." + name + ".text");
        final String rawSeparatorMessage = messages.getFormattedMessage("claim." + name + ".separator");
        final String rawHoverTextTemplate = messages.getFormattedMessage("claim." + name + ".hover");

        final List<BaseComponent> seperatorComponents =  Arrays.asList(TextComponent.fromLegacyText(rawSeparatorMessage));

        final List<BaseComponent> regionsComponents = new LinkedList<>();

        for (ProtectedRegion overlapRegion : intersecting) {
            if(!regionsComponents.isEmpty()) {
                regionsComponents.addAll(seperatorComponents);
            }
            final RegionHandler regionHandler = new RegionHandler(overlapRegion.getId());
            if (!regionHandler.isInternalRegion()) {
                // TODO Maybe handle not internal regions
                continue;
            }
            final HandledOwner<?> handledOwner = regionHandler.getOwner(handlerRegistry);
            if (handledOwner == null) {
                // TODO Maybe add a warn ?
                continue;
            }
            
            final ClaimOwner owner = handledOwner.owner();
            final boolean own = owner.own(actor);
            if (!hasOtherPerm && !own) {
                continue;
            }
            final String regionName = owner.getName();
            final TextComponent regionText = new TextComponent();
            final String formattedRegionText = rawRegionTextTemplate.replace("%region%", regionName);
            final List<BaseComponent> regionTextComponents = Arrays.asList(TextComponent.fromLegacyText(formattedRegionText));
            regionText.setExtra(regionTextComponents);
            if (own ? infoPermChecker.checkBase(context) : infoPermChecker.checkOther(context)) {
                if (rawHoverTextTemplate != null) {
                    final String formattedHoverText = rawHoverTextTemplate.replace("%region%", regionName);
                    final Text hoverText = new Text(TextComponent.fromLegacyText(formattedHoverText));
                    regionText.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverText));
                }
                regionText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim info " + handledOwner.handler().getId() + regionName));
            }
            regionsComponents.add(regionText);
        }

        if (regionsComponents.isEmpty()) {
            messages.sendMessage(sender, "claim." + name + ".nothing");
            return;
        }

        // TODO Handle replace
        final ComponentReplacer replacer = new ComponentReplacer(baseMessage);
        replacer.replace("%regions%", regions.toArray(new BaseComponent[0]));
        sender.spigot().sendMessage(replacer.create());
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
