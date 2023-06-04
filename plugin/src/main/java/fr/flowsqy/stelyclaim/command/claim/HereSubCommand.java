package fr.flowsqy.stelyclaim.command.claim;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.command.sender.PhysicCommandSender;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HereSubCommand implements CommandNode {

    private final static String NAME = "here";
    private final static String[] TRIGGERS = new String[]{NAME, "hr"};
    private final ConfigurationFormattedMessages messages;
    private final ProtocolManager protocolManager;

    public HereSubCommand(@NotNull StelyClaimPlugin plugin) {
        messages = plugin.getMessages();
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getArgsLength() != 0) {
            new HelpMessage().sendMessage(context); // TODO Specify here
            return;
        }
        //final PlayerHandler handler = protocolManager.getHandler("player");

        //final Player player = (Player) sender;
        final CommandSender sender = context.getSender().getBukkit();
        final PhysicCommandSender physicCommandSender = context.getSender().getPhysic();
        final Location senderLoc = physicCommandSender.getLocation();
        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(physicCommandSender.getWorld().getName()), sender, messages);

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(
                BlockVector3.at(
                        senderLoc.getBlockX(),
                        senderLoc.getBlockY(),
                        senderLoc.getBlockZ()
                )
        );

        if (!context.hasPermission(getOtherPermission())) {
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
                    messages.sendMessage(sender, "claim." + NAME + ".inside");
                    // TODO Update stats
                    return;
                }
            }
            messages.sendMessage(sender, "claim." + NAME + ".not-inside");
            return; // TODO Update stats
        }

        final String baseMessage = messages.getFormattedMessage("claim." + NAME + ".message");
        final String text = messages.getFormattedMessage("claim." + NAME + ".text");
        final String separatorMessage = messages.getFormattedMessage("claim." + NAME + ".separator");

        if (context.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.INFO))) {
            final String hover = messages.getFormattedMessage("claim." + NAME + ".hover");
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
            if (regions.isEmpty()) {
                messages.sendMessage(sender, "claim." + NAME + ".nothing");
                // TODO Update stats
                return;
            }
            final ComponentReplacer replacer = new ComponentReplacer(baseMessage);
            replacer.replace("%regions%", regions.toArray(new BaseComponent[0]));
            sender.spigot().sendMessage(replacer.create());
            // TODO Update stats
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
            messages.sendMessage(sender, "claim." + NAME + ".nothing");
            // TODO Update stats
            return;
        }

        sender.sendMessage(baseMessage.replace("%regions%", builder.toString()));

        // TODO Update stats
    }

    @Override
    public @NotNull String[] getTriggers() {
        return TRIGGERS;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return NAME;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getSender().isPhysic() && context.hasPermission(getBasePerm());
    }

    @Override
    public boolean canTabComplete(@NotNull CommandContext context) {
        return canExecute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        return Collections.emptyList();
    }

}
