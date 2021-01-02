package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveOwnerSubCommand extends DomainSubCommand {
    public RemoveOwnerSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected void modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
