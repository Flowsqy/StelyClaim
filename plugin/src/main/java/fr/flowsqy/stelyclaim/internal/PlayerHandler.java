package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimInteractHandler;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlayerHandler implements ClaimHandler<PlayerOwner> {

    public final static String ID = "player";
    //private final ConfigurationFormattedMessages messages;

    public PlayerHandler(StelyClaimPlugin plugin) {
        //this.messages = plugin.getMessages();
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public @Nullable ClaimInteractHandler<PlayerOwner> getClaimInteractHandler() {
        return new ClaimInteractHandler<>() {
            @Override
            public @NotNull Optional<PlayerOwner> getOwner(@NotNull Actor actor, @NotNull String commandArg) {
                return Optional.of(new PlayerOwner(OfflinePlayerRetriever.getOfflinePlayer(commandArg)));
            }

            @Override
            public @NotNull Optional<PlayerOwner> getOwner(@NotNull Actor actor, @NotNull Player player) {
                return Optional.of(new PlayerOwner(player));
            }

            @Override
            public @NotNull FormattedMessages getMessages() {
                return new FormattedMessages() {
                    @Override
                    public String getFormattedMessage(@NotNull String path, String... replace) {
                        return null;
                    }

                    @Override
                    public void sendMessage(@NotNull CommandSender sender, @NotNull String path, String... replace) {
                    }

                    @Override
                    public String getMessage(@NotNull String identifier) {
                        return null;
                    }
                };
            }
        };
    }

    @Override
    public @NotNull HandledOwner<PlayerOwner> getOwner(@NotNull String claimIdentifier) {
        final UUID uuid = UUID.fromString(claimIdentifier);
        return new HandledOwner<>(this, new PlayerOwner(Bukkit.getOfflinePlayer(uuid)));
    }


    @Override
    public @NotNull String getIdentifier(@NotNull PlayerOwner owner) {
        return owner.player().getUniqueId().toString();
    }


}
