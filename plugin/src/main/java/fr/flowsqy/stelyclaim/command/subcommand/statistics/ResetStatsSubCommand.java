package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ResetStatsSubCommand extends SubStatsSubCommand {

    public ResetStatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic, statisticManager);
    }

    @Override
    protected boolean executeSub(CommandSender sender, boolean own, String command, String target) {
        final String other = own ? "" : "-other";
        if (command == null) {
            if (statisticManager.remove(target)) {
                messages.sendMessage(sender, "claim.stats.reset-all" + other, "%target%", target);
                statisticManager.saveTask();
                return true;
            }
            messages.sendMessage(sender, "claim.stats.nodata-all" + other, "%target%", target);
        } else {
            if (statisticManager.removeStat(target, command)) {
                messages.sendMessage(sender, "claim.stats.reset" + other, "%target%", "%command%", target, command);
                statisticManager.saveTask();
                return true;
            }
            messages.sendMessage(sender, "claim.stats.nodata" + other, "%target%", "%command%", target, command);
        }
        return false;
    }
}
