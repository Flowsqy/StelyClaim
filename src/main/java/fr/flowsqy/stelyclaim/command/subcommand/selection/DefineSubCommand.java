package fr.flowsqy.stelyclaim.command.subcommand.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class DefineSubCommand extends SelectionSubCommand {

    public DefineSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected boolean checkExistRegion(boolean regionExist, Player player, boolean ownRegion, String regionName, String worldName) {
        if(!regionExist)
            return false;

        messages.sendMessage(player, "claim.alreadyexist" + (ownRegion ? "" : "other"), "%region%", regionName);
        return true;
    }

    @Override
    protected void manageRegion(Player player, ProtectedRegion region, ProtectedCuboidRegion newRegion, boolean ownRegion, RegionManager regionManager, String regionName) {
        configModifyRegion(newRegion, "define", player, regionName);

        regionManager.addRegion(newRegion);

        messages.sendMessage(player, "claim.define" + (ownRegion ? "" : "other"), "%region%", regionName);
    }


}
