package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AddMemberSubCommand extends DomainSubCommand {

    public AddMemberSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected void modifyRegion(ProtectedRegion region, String targetPlayer, boolean ownRegion) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
