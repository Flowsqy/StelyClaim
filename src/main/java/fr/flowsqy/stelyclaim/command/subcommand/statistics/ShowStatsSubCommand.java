package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.StatisticManager;

import java.util.List;

public class ShowStatsSubCommand extends SubStatsSubCommand{
    public ShowStatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic, statisticManager);
    }
}
