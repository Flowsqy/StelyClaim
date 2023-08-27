package fr.flowsqy.stelyclaim.pillar.cuboid;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.pillar.PillarManager;
import fr.flowsqy.stelyclaim.pillar.PillarSession;
import fr.flowsqy.stelyclaim.util.ComponentReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CuboidPillarTextSender {

    private static final String NORTHWEST = "northwest";
    private static final String NORTHEAST = "northeast";
    private static final String SOUTHWEST = "southwest";
    private static final String SOUTHEAST = "southeast";
    private static final String CURRENT = "current";

    private final ConfigurationFormattedMessages messages;
    private final PillarManager pillarManager;
    private final String category;

    public CuboidPillarTextSender(@NotNull ConfigurationFormattedMessages messages, @NotNull PillarManager pillarManager, @NotNull String category) {
        this.messages = messages;
        this.pillarManager = pillarManager;
        this.category = category;
    }

    @Nullable
    private TextComponent buildTeleportationComponent(@NotNull String direction) {
        final String message = messages.getFormattedMessage("pillar." + category + "." + direction + ".message");
        if (message == null) {
            return null;
        }
        final TextComponent mainComponent = new TextComponent();
        for (BaseComponent component : TextComponent.fromLegacyText(message)) {
            mainComponent.addExtra(component);
        }
        final String text = messages.getFormattedMessage("pillar." + category + "." + direction + ".hover");
        if (text != null) {
            mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));
        }
        return mainComponent;
    }

    public boolean sendMessage(@NotNull Player player, @NotNull ProtectedRegion newRegion) {
        return sendMessage(player, new CuboidPillarCoordinate(newRegion, player.getWorld()));
    }

    public boolean sendMessage(@NotNull Player player, @NotNull CuboidPillarCoordinate pillarCoordinate) {
        final BaseComponent[] pillarMessage = buildMessage(player, pillarCoordinate);
        if (pillarMessage == null) {
            return false;
        }
        player.spigot().sendMessage(pillarMessage);
        return true;
    }

    @Nullable
    private BaseComponent[] buildMessage(@NotNull Player player, @NotNull CuboidPillarCoordinate pillarCoordinate) {
        final String mainMessage = messages.getFormattedMessage("pillar." + category + ".message");
        if (mainMessage == null) {
            return null;
        }
        BaseComponent[] mainComponents = TextComponent.fromLegacyText(mainMessage);
        final PillarSession pillarSession = pillarManager.getOrCreateSession(player.getUniqueId());
        return replaceTeleportPlaceholders(mainComponents, pillarSession, pillarCoordinate, player.getLocation());
    }

    @NotNull
    private BaseComponent[] replaceTeleportPlaceholders(@NotNull BaseComponent[] mainComponents, @NotNull PillarSession pillarSession, @NotNull CuboidPillarCoordinate pillarCoordinate, @NotNull Location currentLocation) {
        mainComponents = replacePillarMessage(mainComponents, NORTHWEST, pillarSession, pillarCoordinate::getNorthWestLocation);
        mainComponents = replacePillarMessage(mainComponents, NORTHEAST, pillarSession, pillarCoordinate::getNorthEastLocation);
        mainComponents = replacePillarMessage(mainComponents, SOUTHWEST, pillarSession, pillarCoordinate::getSouthWestLocation);
        mainComponents = replacePillarMessage(mainComponents, SOUTHEAST, pillarSession, pillarCoordinate::getSouthEastLocation);
        mainComponents = replacePillarMessage(mainComponents, CURRENT, pillarSession, () -> currentLocation);
        return mainComponents;
    }

    @NotNull
    private BaseComponent[] replacePillarMessage(@NotNull BaseComponent[] mainComponents, @NotNull String place, @NotNull PillarSession pillarSession, @NotNull Supplier<Location> locationSupplier) {
        final TextComponent placeComponent = buildTeleportationComponent(place);
        if (placeComponent == null) {
            return mainComponents;
        }
        return linkPillarMessage(mainComponents, place, placeComponent, pillarSession, locationSupplier.get());
    }

    @NotNull
    private BaseComponent[] linkPillarMessage(@NotNull BaseComponent[] mainMessage, @NotNull String direction, @NotNull TextComponent replacement, @NotNull PillarSession pillarSession, @NotNull Location location) {
        final int index = pillarSession.registerLocation(location);
        // TODO Hardcoded -> Bad
        replacement.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim pillar " + index));
        return ComponentReplacer.replace(mainMessage, "%" + direction + "%", new BaseComponent[]{replacement});
    }

}
