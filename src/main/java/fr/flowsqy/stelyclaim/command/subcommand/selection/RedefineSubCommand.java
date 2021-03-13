package fr.flowsqy.stelyclaim.command.subcommand.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class RedefineSubCommand extends SelectionSubCommand {

    public RedefineSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName) {
        if(regionExist)
            return false;

        messages.sendMessage(player, "claim.exist.not" + (ownRegion ? "" : "-other"), "%region%", regionName);
        return true;
    }

    @Override
    protected void checkIntegrateRegion(boolean overlapSame, Player player) {
        if(!overlapSame){
            messages.sendMessage(player, "claim.selection.redefinenotoverlap");
        }
    }

    @Override
    protected void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager, String regionName) {

        newRegion.copyFrom(region);
        configModifyRegion(newRegion, "redefine", player, regionName);
        regionManager.addRegion(newRegion);

        messages.sendMessage(player, "claim.command.redefine" + (ownRegion ? "" : "-other"), "%region%", regionName);
    }
}
