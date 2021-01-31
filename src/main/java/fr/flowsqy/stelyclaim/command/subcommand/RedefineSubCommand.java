package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

public class RedefineSubCommand extends SelectionSubCommand {

    public RedefineSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    protected boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName) {
        if(regionExist)
            return false;

        messages.sendMessage(player, "claim.notexist" + (ownRegion ? "" : "other"), "%region%", regionName);
        return true;
    }

    @Override
    protected void checkIntegrateRegion(boolean overlapSame, Player player) {
        if(!overlapSame){
            messages.sendMessage(player, "claim.redefinenotoverlap");
        }
    }

    @Override
    protected void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager) {
        //TODO Setup region by config

        if(region instanceof ProtectedCuboidRegion){
            final ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;
            cuboidRegion.setMaximumPoint(newRegion.getMaximumPoint());
            cuboidRegion.setMinimumPoint(newRegion.getMinimumPoint());
        }
        else {
            newRegion.copyFrom(region);
            regionManager.addRegion(newRegion);
        }

        messages.sendMessage(player, "claim.redefine" + (ownRegion ? "" : "other"), "%region%", newRegion.getId());
    }
}