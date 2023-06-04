package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.util.PillarData;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PillarSubCommand implements CommandNode<ClaimContextData> {

    private final static String NAME = "pillar";
    private final static String[] TRIGGERS = new String[]{NAME};
    private final Map<String, PillarData> pillarData;
    private final TeleportSync teleportSync;

    public PillarSubCommand(@NotNull StelyClaimPlugin plugin) {
        this.pillarData = plugin.getPillarData();
        this.teleportSync = plugin.getTeleportSync();
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        final HelpMessage helpMessage = new HelpMessage();
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
        context.getData().setStatistic(NAME);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return TRIGGERS;
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
