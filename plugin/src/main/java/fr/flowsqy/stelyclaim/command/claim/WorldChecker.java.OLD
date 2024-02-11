package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WorldChecker {

    private final Set<String> worlds;
    private final String denyMessage;

    public WorldChecker(@Nullable Collection<String> worlds, @NotNull ConfigurationFormattedMessages messages) {
        this(worlds, messages.getFormattedMessage("claim.world.notallowed"));
    }

    public WorldChecker(@Nullable Collection<String> worlds, @Nullable String denyMessage) {
        this.worlds = worlds == null || worlds.isEmpty() ? null : new HashSet<>(worlds);
        this.denyMessage = denyMessage;
    }

    public boolean checkCancelledWorld(@NotNull Actor actor) {
        if (worlds != null && actor.isPhysic() && !worlds.contains(actor.getPhysic().getWorld().getName())) {
            if (denyMessage != null) {
                actor.getBukkit().sendMessage(denyMessage);
            }
            return true;
        }
        return false;
    }

}
