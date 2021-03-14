package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StatsSubCommand extends SubCommand {

    private final StatisticManager statisticManager;

    public StatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        this.statisticManager = statisticManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
