package fr.flowsqy.stelyclaim.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PillarTextSender {

    private static final String NORTHWEST = "northwest";
    private static final String NORTHEAST = "northeast";
    private static final String SOUTHWEST = "southwest";
    private static final String SOUTHEAST = "southeast";
    private static final String CURRENT = "current";

    private final String baseMessage;
    private final TextComponent northwestTxtCpnt;
    private final TextComponent northeastTxtCpnt;
    private final TextComponent southwestTxtCpnt;
    private final TextComponent southeastTxtCpnt;
    private final TextComponent currentTxtCpnt;

    private final Map<String, PillarData> pillarData;

    public PillarTextSender(ConfigurationFormattedMessages messages, String category, Map<String, PillarData> pillarData) {
        baseMessage = messages.getFormattedMessage("pillar." + category + ".message");
        if (baseMessage != null) {
            northwestTxtCpnt = createTextComponent(messages, category, NORTHWEST);
            northeastTxtCpnt = createTextComponent(messages, category, NORTHEAST);
            southwestTxtCpnt = createTextComponent(messages, category, SOUTHWEST);
            southeastTxtCpnt = createTextComponent(messages, category, SOUTHEAST);
            currentTxtCpnt = createTextComponent(messages, category, CURRENT);
        } else {
            northwestTxtCpnt = null;
            northeastTxtCpnt = null;
            southwestTxtCpnt = null;
            southeastTxtCpnt = null;
            currentTxtCpnt = null;
        }

        this.pillarData = pillarData;
    }

    private TextComponent createTextComponent(ConfigurationFormattedMessages messages, String category, String direction) {
        final String message = messages.getFormattedMessage("pillar." + category + "." + direction + ".message");
        final TextComponent textComponent = new TextComponent();
        if (message == null)
            return textComponent;
        textComponent.setExtra(
                new ArrayList<>(Arrays.asList(
                        TextComponent.fromLegacyText(message)
                ))
        );
        final String text = messages.getFormattedMessage("pillar." + category + "." + direction + ".hover");
        if (text != null) {
            textComponent.setHoverEvent(
                    new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new Text(text)
                    )
            );
        }
        return textComponent;
    }

    public ComponentReplacer getReplacer(Player player, PillarCoordinate pillarCoordinate) {
        if (baseMessage != null) {
            PillarData pillarData = this.pillarData.get(player.getName());
            if (pillarData == null) {
                pillarData = new PillarData();
                this.pillarData.put(player.getName(), pillarData);
            }

            final ComponentReplacer replacer = new ComponentReplacer(baseMessage);

            if (northwestTxtCpnt != null) {
                buildPillarMessage("%northwest%", northwestTxtCpnt, pillarCoordinate.getNorthWestBlockLocation(), pillarData, replacer);
            }
            if (northeastTxtCpnt != null) {
                buildPillarMessage("%northeast%", northeastTxtCpnt, pillarCoordinate.getNorthEastBlockLocation(), pillarData, replacer);
            }
            if (southwestTxtCpnt != null) {
                buildPillarMessage("%southwest%", southwestTxtCpnt, pillarCoordinate.getSouthWestBlockLocation(), pillarData, replacer);
            }
            if (southeastTxtCpnt != null) {
                buildPillarMessage("%southeast%", southeastTxtCpnt, pillarCoordinate.getSouthEastBlockLocation(), pillarData, replacer);
            }
            if (currentTxtCpnt != null) {
                buildPillarMessage("%current%", currentTxtCpnt, player.getLocation(), pillarData, replacer);
            }

            return replacer;
        }
        return null;
    }

    public boolean sendMessage(Player player, ProtectedRegion newRegion) {
        return sendMessage(player, new PillarCoordinate(newRegion, player.getWorld()));
    }

    public boolean sendMessage(Player player, PillarCoordinate pillarCoordinate) {
        final ComponentReplacer replacer = getReplacer(player, pillarCoordinate);
        if (replacer != null) {
            player.spigot().sendMessage(replacer.create());
            return true;
        }
        return false;
    }

    private void buildPillarMessage(String regex, TextComponent textComponent, Location location, PillarData pillarData, ComponentReplacer replacer) {
        final String id = pillarData.registerLocation(location);
        final TextComponent component = textComponent.duplicate();
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim pillar " + id));
        replacer.replace(regex, new TextComponent[]{component});
    }

}
