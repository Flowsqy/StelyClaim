package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StatsSubCommand extends DispatchCommandTabExecutor<ClaimContextData> implements CommandNode<ClaimContextData> {

    private final static String NAME = "stats";
    private final static String[] TRIGGERS = new String[]{NAME, "s"};
    private final List<CommandNode<ClaimContextData>> children;

    public StatsSubCommand(@NotNull StelyClaimPlugin plugin) {
        children = Arrays.asList(new ShowStatsSubCommand(plugin), new ResetStatsSubCommand(plugin));
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
        // Send help
        new HelpMessage().sendMessage(context); // TODO Specify stats
    }

    @Override
    public @NotNull String[] getTriggers() {
        return TRIGGERS;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return NAME;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.hasPermission(getBasePerm());
    }

}
