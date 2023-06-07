package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.PermissionData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatsSubCommand extends DispatchCommandTabExecutor<ClaimContextData> implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final PermissionData data;
    private final HelpMessage helpMessage;
    private final List<CommandNode<ClaimContextData>> children;

    public StatsSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull PermissionData data, @NotNull HelpMessage helpMessage, @NotNull List<CommandNode<ClaimContextData>> children) {
        this.name = name;
        this.triggers = triggers;
        this.data = data;
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
    public @NotNull List<CommandNode<ClaimContextData>> getChildren() {
        return children;
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContextData> context) {
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
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.hasPermission(data.getBasePerm(context.getData()));
    }

}
