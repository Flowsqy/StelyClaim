package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

public class InfoSubCommand extends InteractSubCommand {

    public InfoSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected void interactRegion(Player player, ProtectedRegion region, boolean ownRegion) {

    }


}
