package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StatsSubCommand extends SubCommand {
    public StatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
