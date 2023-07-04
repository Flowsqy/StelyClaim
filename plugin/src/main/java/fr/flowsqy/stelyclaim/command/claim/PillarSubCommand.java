package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.protocol.ClaimContextData;
import fr.flowsqy.stelyclaim.util.PillarData;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PillarSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final HelpMessage helpMessage;
    private final Map<String, PillarData> pillarData;
    private final TeleportSync teleportSync;

    public PillarSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        this.helpMessage = helpMessage;
        pillarData = plugin.getPillarData();
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() != 1) {
            helpMessage.sendMessage(context);
            return;
        }
        final PillarData pillarData = this.pillarData.get(context.getSender().getBukkit().getName());
        if (pillarData == null) {
            helpMessage.sendMessage(context);
            return;
        }
        final String arg = context.getArg(0);
        if (arg.length() != 1) {
            helpMessage.sendMessage(context);
            return;
        }
        final Location loc = pillarData.getLocations().get(arg);
        if (loc == null) {
            helpMessage.sendMessage(context);
            return;
        }
        if (loc.getWorld() == null) // Normally impossible
            return;

        final Location teleportLoc = loc.clone();
        if (teleportLoc.getX() == Math.floor(teleportLoc.getX())) {
            // Correct position only for pillar loc (exclude current position)
            teleportLoc.setY(loc.getWorld().getHighestBlockYAt(loc));
            teleportLoc.add(0.5, 1, 0.5);
        }
        context.getSender().getMovable().setLocation(teleportSync, teleportLoc);
        context.getData().setStatistic(name);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getTabCompletion() {
        throw new IllegalStateException();
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.getSender().isMovable();
    }

    @Override
    public boolean canTabComplete(@NotNull CommandContext<ClaimContextData> context) {
        return false;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        throw new IllegalStateException();
    }

}
