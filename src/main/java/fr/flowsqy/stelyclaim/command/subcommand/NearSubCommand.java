package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class NearSubCommand extends SubCommand {

    public NearSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {

        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return Collections.emptyList();
    }
}
