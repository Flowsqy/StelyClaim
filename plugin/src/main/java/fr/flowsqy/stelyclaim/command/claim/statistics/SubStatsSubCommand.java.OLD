package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SubStatsSubCommand implements CommandNode, Identifiable {

    protected final ConfigurationFormattedMessages messages;
    protected final StatisticManager statisticManager;
    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final OtherPermissionChecker permChecker;
    private final HelpMessage helpMessage;

    public SubStatsSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                              @NotNull StelyClaimPlugin plugin, @NotNull OtherPermissionChecker permChecker,
                              @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
        messages = plugin.getMessages();
        statisticManager = plugin.getStatisticManager();
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        final Actor actor = context.getActor();
        final OfflinePlayer target;
        final String command;
        switch (context.getArgsLength()) {
            case 0 -> {
                if (!actor.isPlayer()) {
                    helpMessage.sendMessage(context, id);
                    return;
                }
                target = actor.getPlayer();
                command = null;
            }
            case 1 -> {
                if (permChecker.checkOther(context)) {
                    target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
                    command = null;
                } else if (!actor.isPlayer()) {
                    helpMessage.sendMessage(context, id);
                    return;
                } else {
                    target = actor.getPlayer();
                    command = context.getArg(0).toLowerCase(Locale.ENGLISH);
                }
            }
            case 2 -> {
                target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
                command = context.getArg(1).toLowerCase(Locale.ENGLISH);
            }
            default -> {
                helpMessage.sendMessage(context, id);
                return;
            }
        }
        final boolean own = actor.isPlayer() && actor.getPlayer().getUniqueId().equals(target.getUniqueId());
        if (!own && !permChecker.checkOther(context)) {
            helpMessage.sendMessage(context, id);
            return;
        }
        if (command != null) {
            /*
             * if (!commandsName.contains(command)) {
             * //messages.sendMessage(sender, "claim.stats.commandnotexist", "%command%",
             * command);
             * return false;
             * }
             */
            if (!statisticManager.allowStats(command)) {
                messages.sendMessage(actor.getBukkit(), "claim.stats.commandnotstat", "%command%", command);
                return;
            }
        }
        final boolean success = process(context, own, command, target);
        if (success) {
            // TODO Handle stats
            // contextual.getData().setStatistic("stats-" + name);
        }
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
        return permChecker.checkBase(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        final String cmdArg;
        if (context.getArgsLength() == 1) {
            if (permChecker.checkOther(context)) {
                return null;
            }
            cmdArg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        } else if (context.getArgsLength() == 2) {
            if (!permChecker.checkOther(context)) {
                return Collections.emptyList();
            }
            cmdArg = context.getArg(1).toLowerCase(Locale.ENGLISH);
        } else {
            return Collections.emptyList();
        }
        return Stream.of(statisticManager.getCommands())
                .filter(cmd -> cmd.startsWith(cmdArg))
                .collect(Collectors.toList());
    }

    protected abstract boolean process(@NotNull CommandContext context, boolean own,
                                       @Nullable String command, @NotNull OfflinePlayer target);

}
