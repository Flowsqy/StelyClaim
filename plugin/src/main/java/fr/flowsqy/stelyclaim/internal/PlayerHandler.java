package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerHandler implements ClaimHandler<PlayerOwner> {

    public final static String ID = "player";

    private final ConfigPlayerModifier defineModifier;
    private final ConfigPlayerModifier redefineModifier;
    private final ConfigurationFormattedMessages messages;

    public PlayerHandler(StelyClaimPlugin plugin) {
        this.defineModifier = new ConfigPlayerModifier(plugin.getConfiguration(), plugin.getMessages(), "define");
        this.redefineModifier = new ConfigPlayerModifier(plugin.getConfiguration(), plugin.getMessages(), "redefine");
        this.messages = plugin.getMessages();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public PlayerOwner getOwner(String claimIdentifier) {
        final UUID uuid = UUID.fromString(claimIdentifier);
        return new PlayerOwner(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public String getIdentifier(PlayerOwner owner) {
        return owner.player().getUniqueId().toString();
    }

    @Override
    public RegionModifier<PlayerOwner> getDefineModifier() {
        return defineModifier;
    }

    @Override
    public RegionModifier<PlayerOwner> getRedefineModifier() {
        return redefineModifier;
    }

    @Override
    public FormattedMessages getMessages() {
        return messages;
    }
}