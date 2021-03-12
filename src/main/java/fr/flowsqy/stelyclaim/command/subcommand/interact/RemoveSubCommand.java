package fr.flowsqy.stelyclaim.command.subcommand.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveSubCommand extends InteractSubCommand {
    public RemoveSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected void interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion, String regionName) {
        regionManager.removeRegion(region.getId());

        messages.sendMessage(player, "claim.remove" + (ownRegion ? "" : "other"), "%region%", regionName);
        if(!ownRegion)
            plugin.getMailManager().sendInfoToTarget(player, regionName, getName());
    }
}
