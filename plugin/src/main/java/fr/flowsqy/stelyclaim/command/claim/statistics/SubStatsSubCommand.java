package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SubStatsSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    protected final ConfigurationFormattedMessages messages;
    protected final StatisticManager statisticManager;
    //private final Set<String> commandsName;

    public SubStatsSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin) {
        this.name = name;
        this.triggers = triggers;
        messages = plugin.getMessages();
        statisticManager = plugin.getStatisticManager();
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        final OfflinePlayer target;
        final String command;
        switch (context.getArgsLength()) {
            case 0 -> {
                if (!context.getSender().isPlayer()) {
                    new HelpMessage().sendMessage(context); // TODO Specify stats + name
                    return;
                }
                target = context.getSender().getPlayer();
                command = null;
            }
            case 1 -> {
                if (context.hasPermission(getOtherPerm())) {
                    target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
                    command = null;
                } else if (!context.getSender().isPlayer()) {
                    new HelpMessage().sendMessage(context); // TODO Specify stats + name
                    return;
                } else {
                    target = context.getSender().getPlayer();
                    command = context.getArg(0).toLowerCase(Locale.ENGLISH);
                }
            }
            case 2 -> {
                target = OfflinePlayerRetriever.getOfflinePlayer(context.getArg(0));
                command = context.getArg(1).toLowerCase(Locale.ENGLISH);
            }
            default -> {
                new HelpMessage().sendMessage(context); // TODO Specify stats + name
                return;
            }
        }
        final boolean own = context.getSender().isPlayer() && context.getSender().getPlayer().getUniqueId().equals(target.getUniqueId());
        if (!own && !context.hasPermission(getOtherPerm())) {
            new HelpMessage().sendMessage(context); // TODO Specify stats + name
            return;
        }
        if (command != null) {
            /*
            if (!commandsName.contains(command)) {
                //messages.sendMessage(sender, "claim.stats.commandnotexist", "%command%", command);
                return false;
            }*/
            if (!statisticManager.allowStats(command)) {
                messages.sendMessage(context.getSender().getBukkit(), "claim.stats.commandnotstat", "%command%", command);
                return;
            }
        }
        final boolean success = process(context, own, command, target);
        if (success) {
            context.getData().setStatistic("stats-" + name);
        }
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.hasPermission(getBasePerm());
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() == 1 && context.hasPermission(getOtherPerm)) {
            return null;
        }
        final String cmdArg;
        if (context.getArgsLength() == 1) {
            if (context.hasPermission(getOtherPerm())) {
                return null;
            }
            cmdArg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        } else if (context.getArgsLength() == 2) {
            if (!context.hasPermission(getOtherPerm())) {
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

    protected abstract boolean process(@NotNull CommandContext<ClaimContextData> context, boolean own, @Nullable String command, @NotNull OfflinePlayer target);

}
