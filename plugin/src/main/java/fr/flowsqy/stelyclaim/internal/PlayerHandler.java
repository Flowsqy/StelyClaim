package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimInteractHandler;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlayerHandler implements ClaimHandler<PlayerOwner> {

    public final static String ID = "player";

    private final ConfigPlayerModifier defineModifier;
    private final ConfigPlayerModifier redefineModifier;
    //private final ConfigurationFormattedMessages messages;

    public PlayerHandler(StelyClaimPlugin plugin) {
        this.defineModifier = new ConfigPlayerModifier(plugin.getConfiguration(), plugin.getMessages(), "define");
        this.redefineModifier = new ConfigPlayerModifier(plugin.getConfiguration(), plugin.getMessages(), "redefine");
        //this.messages = plugin.getMessages();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public @Nullable ClaimInteractHandler<PlayerOwner> getClaimInteractHandler() {
        return new ClaimInteractHandler<>() {
            @Override
            public @NotNull Optional<PlayerOwner> getOwner(@NotNull Actor actor, @NotNull String commandArg) {
                final OfflinePlayer p = OfflinePlayerRetriever.getOfflinePlayer(commandArg);
                if (p == null) {
                    return Optional.empty();
                }
                return Optional.of(new PlayerOwner(p));
            }

            @Override
            public @NotNull Optional<PlayerOwner> getOwner(@NotNull Actor actor, @NotNull Player player) {
                return Optional.of(new PlayerOwner(player));
            }
        };
    }

    @Override
    public @NotNull HandledOwner<PlayerOwner> getOwner(@NotNull String claimIdentifier) {
        final UUID uuid = UUID.fromString(claimIdentifier);
        return new HandledOwner<>(this, new PlayerOwner(Bukkit.getOfflinePlayer(uuid)));
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


}
