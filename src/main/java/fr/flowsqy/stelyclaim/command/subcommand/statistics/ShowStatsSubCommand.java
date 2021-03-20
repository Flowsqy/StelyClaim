package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ShowStatsSubCommand extends SubStatsSubCommand{

    public ShowStatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic, statisticManager);
    }

    @Override
    protected boolean executeSub(CommandSender sender, boolean own, String command, String target) {
        if(command == null){
            final String other = (own ? "" : "-other");
            final String path = "claim.stats.show" + other;
            for(String statCommand : statisticManager.getCommands()){
                messages.sendMessage(
                        sender,
                        path,
                        "%command%", "%target%", "%stat%",
                        statCommand, target, String.valueOf(statisticManager.get(target, statCommand))
                );
            }
            messages.sendMessage(
                    sender,
                    "claim.stats.show-total" + other,
                    "%target%", "%stat%",
                    target, String.valueOf(statisticManager.getTotal(target))
            );
        }
        else {
            messages.sendMessage(
                    sender,
                    "claim.stats.show" + (own ? "" : "-other"),
                    "%command%", "%target%", "%stat%",
                    command, target, String.valueOf(statisticManager.get(target, command))
            );
        }

        return true;
    }
}
