package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ResetStatsSubCommand extends SubStatsSubCommand{

    public ResetStatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic, statisticManager);
    }

    @Override
    protected boolean executeSub(CommandSender sender, boolean own, String command, String target) {
        return false;
    }
}
