package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.command.claim.permission.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatsSubCommand extends DispatchCommandTabExecutor<ClaimContext> implements CommandNode<ClaimContext> {

    private final String name;
    private final String[] triggers;
    private final CommandPermissionChecker permChecker;
    private final HelpMessage helpMessage;
    private final List<CommandNode<ClaimContext>> children;

    public StatsSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull CommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage, @NotNull List<CommandNode<ClaimContext>> children) {
        this.name = name;
        this.triggers = triggers;
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
        this.children = new ArrayList<>(children);
    }

    /*
        this.resetStatsSubCommand = new ResetStatsSubCommand(
                plugin,
                "reset",
                "r",
                permission + ".reset",
                console,
                plugin.getConfiguration().getStringList("worlds.stats.reset"),
                statistic,
                statisticManager
        );
        this.showStatsSubCommand = new ShowStatsSubCommand(
                plugin,
                "show",
                "s",
                permission + ".show",
                console,
                plugin.getConfiguration().getStringList("worlds.stats.show"),
                statistic,
                statisticManager
        );*/

    @Override
    public @NotNull Iterable<CommandNode<ClaimContext>> getChildren() {
        return children;
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContext> context) {
        helpMessage.sendMessage(context, name);
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
    public boolean canExecute(@NotNull CommandContext<ClaimContext> context) {
        return permChecker.checkBase(context);
    }

}
