package fr.flowsqy.stelyclaim.command.subcommand.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import org.bukkit.entity.Player;

import java.util.List;

public class DefineSubCommand extends SelectionSubCommand {

    public DefineSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected boolean process(Player player, PlayerHandler handler, PlayerOwner owner) {
        return plugin.getProtocolManager().define(player, handler, owner);
    }


}
