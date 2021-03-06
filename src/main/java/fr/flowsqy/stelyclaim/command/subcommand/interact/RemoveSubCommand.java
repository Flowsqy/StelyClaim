package fr.flowsqy.stelyclaim.command.subcommand.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

public class RemoveSubCommand extends InteractSubCommand {
    public RemoveSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected void interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion) {
        final String regionName = region.getId();
        regionManager.removeRegion(region.getId());

        messages.sendMessage(player, "claim.remove" + (ownRegion ? "" : "other"), "%region%", region.getId());
        if(!ownRegion)
            plugin.getMailManager().sendInfoToTarget(player, regionName, getName());
    }
}
