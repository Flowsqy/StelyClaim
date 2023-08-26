package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.pillar.PillarData;
import fr.flowsqy.stelyclaim.pillar.PillarManager;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PillarSubCommand implements CommandNode {

    private final String name;
    private final String[] triggers;
    private final HelpMessage helpMessage;
    private final DispatchCommandTabExecutor root;
    private final PillarManager pillarManager;
    private final TeleportSync teleportSync;

    public PillarSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin,
                            @NotNull HelpMessage helpMessage, @NotNull DispatchCommandTabExecutor root) {
        this.name = name;
        this.triggers = triggers;
        this.helpMessage = helpMessage;
        this.root = root;
        pillarManager = plugin.getPillarManager();
        teleportSync = plugin.getTeleportSync();
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getArgsLength() != 1 || !context.getActor().isPlayer()) {
            sendGlobalHelp(context);
            return;
        }
        final PillarData pillarData = pillarManager.getSession(context.getActor().getPlayer().getUniqueId());
        if (pillarData == null) {
            sendGlobalHelp(context);
            return;
        }
        final String arg = context.getArg(0);
        if (arg.length() != 1) {
            sendGlobalHelp(context);
            return;
        }
        final int index = fastParse(arg.charAt(0));
        if (index < 0) {
            sendGlobalHelp(context);
            return;
        }
        final Location loc = pillarData.get(index);
        if (loc == null) {
            sendGlobalHelp(context);
            return;
        }
        final World world = loc.getWorld();
        if (world == null) // Normally impossible
            return;

        context.getActor().getMovable().setLocation(teleportSync, loc);
        // context.getData().setStatistic(name);
    }

    private int fastParse(char c) {
        return switch (c) {
            case '0' -> 0;
            case '1' -> 1;
            case '2' -> 2;
            case '3' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '7' -> 7;
            case '8' -> 8;
            case '9' -> 9;
            default -> -1;
        };
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getActor().isPlayer();
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
        helpMessage.sendMessages(fakeContext, root, CommandNode::canExecute);
    }

}
