package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class WorldSubCommand extends NamedSubCommand {

    private final Set<String> worlds;
    protected final ConfigurationFormattedMessages messages;

    public WorldSubCommand(@NotNull String name, @NotNull String[] aliases, @NotNull Collection<String> worlds, @NotNull ConfigurationFormattedMessages messages) {
        super(name, aliases);
        this.worlds = worlds.isEmpty() ? null : new HashSet<>(worlds);
        this.messages = messages;
    }

    protected boolean checkCancelledWorld(@NotNull CommandContext<ClaimContextData> context) {
        if (worlds != null && context.getSender().isPhysic() && !worlds.contains(context.getSender().getPhysic().getWorld().getName())) {
            messages.sendMessage(context.getSender().getBukkit(), "claim.world.notallowed");
            return true;
        }
        return false;
    }

}
