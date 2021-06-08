package fr.flowsqy.stelyclaim.intern;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerHandler implements ClaimHandler<PlayerOwner> {

    public final static String ID = "player";

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
        return null;
    }

    @Override
    public RegionModifier<PlayerOwner> getRedefineModifier() {
        return null;
    }

    @Override
    public ClaimMessage getMessages() {
        return null;
    }
}
