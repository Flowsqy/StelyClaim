package fr.flowsqy.stelyclaim.command.claim;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.util.PillarData;
import fr.flowsqy.stelyclaim.util.TeleportSync;

public class PillarSubCommand implements CommandNode {

    private final String[] triggers;
    private final HelpMessage helpMessage;
    private final DispatchCommandTabExecutor root;
    private final Map<String, PillarData> pillarData;
    private final TeleportSync teleportSync;

    public PillarSubCommand(@NotNull String[] triggers, @NotNull StelyClaimPlugin plugin,
            @NotNull HelpMessage helpMessage, @NotNull DispatchCommandTabExecutor root) {
        this.triggers = triggers;
        this.helpMessage = helpMessage;
        this.root = root;
        pillarData = plugin.getPillarData();
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getArgsLength() != 1) {
            sendGlobalHelp(context);
            return;
        }
        final PillarData pillarData = this.pillarData.get(context.getActor().getBukkit().getName());
        if (pillarData == null) {
            sendGlobalHelp(context);
            return;
        }
        final String arg = context.getArg(0);
        if (arg.length() != 1) {
            sendGlobalHelp(context);
            return;
        }
        final Location loc = pillarData.getLocations().get(arg);
        if (loc == null) {
            sendGlobalHelp(context);
            return;
        }
        final World world = loc.getWorld();
        if (world == null) // Normally impossible
            return;

        final Location teleportLoc = loc.clone();
        if (teleportLoc.getX() == Math.floor(teleportLoc.getX())) {
            // Correct position only for pillar loc (exclude current position)
            teleportLoc.setY(world.getHighestBlockYAt(loc));
            teleportLoc.add(0.5, 1, 0.5);
        }
        context.getActor().getMovable().setLocation(teleportSync, teleportLoc);
        // context.getData().setStatistic(name);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getName() {
        throw new IllegalStateException();
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getActor().isMovable();
    }

    @Override
    public boolean canTabComplete(@NotNull CommandContext context) {
        return false;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        throw new IllegalStateException();
    }

    private void sendGlobalHelp(@NotNull CommandContext context) {
        // TODO Hardcoded -> bad
        final CommandContext fakeContext = CommandContext.buildFake(context, new String[0], "claim");
        helpMessage.sendMessages(fakeContext, root);
    }

}
