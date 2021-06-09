package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerHandler implements ClaimHandler<PlayerOwner> {

    public final static String ID = "player";

    private final ConfigPlayerModifier defineModifier;
    private final ConfigPlayerModifier redefineModifier;
    private final DefaultClaimMessages messages;

    public PlayerHandler(Messages messages) {
        this.defineModifier = new ConfigPlayerModifier("define");
        this.redefineModifier = new ConfigPlayerModifier("redefine");
        this.messages = new DefaultClaimMessages(messages);
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
        return owner.getPlayer().getUniqueId().toString();
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
    public ClaimMessage getMessages() {
        return messages;
    }
}
