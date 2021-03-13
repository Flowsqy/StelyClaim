package fr.flowsqy.stelyclaim.command.subcommand.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.componentreplacer.ComponentReplacer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.util.PillarCoordinate;
import fr.flowsqy.stelyclaim.util.PillarData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class RedefineSubCommand extends SelectionSubCommand {

    private final String pillarMessage;
    private final TextComponent pillarNWTxtCpnt;
    private final TextComponent pillarNETxtCpnt;
    private final TextComponent pillarSWTxtCpnt;
    private final TextComponent pillarSETxtCpnt;

    public RedefineSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        pillarMessage = messages.getMessage("pillar.previous.message");
        if(pillarMessage != null) {
            pillarNWTxtCpnt = createTextComponent("previous", "northwest");
            pillarNETxtCpnt = createTextComponent("previous", "northeast");
            pillarSWTxtCpnt = createTextComponent("previous", "southwest");
            pillarSETxtCpnt = createTextComponent("previous", "southeast");
        }
        else{
            pillarNWTxtCpnt = null;
            pillarNETxtCpnt = null;
            pillarSWTxtCpnt = null;
            pillarSETxtCpnt = null;
        }
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

        // Previous pillar manage

        if(pillarMessage != null) {
            final PillarCoordinate pillarCoordinate = new PillarCoordinate(region, player.getWorld());
            PillarData pillarData = this.pillarData.get(player.getName());
            if(pillarData == null){
                pillarData = new PillarData();
                this.pillarData.put(player.getName(), pillarData);
            }

            final ComponentReplacer replacer = new ComponentReplacer(pillarMessage);

            if (pillarNWTxtCpnt != null) {
                buildPillarMessage("%northwest%", pillarNWTxtCpnt, pillarCoordinate.getNorthWestBlockLocation(), pillarData, replacer);
            }
            if (pillarNETxtCpnt != null) {
                buildPillarMessage("%northeast%", pillarNETxtCpnt, pillarCoordinate.getNorthEastBlockLocation(), pillarData, replacer);
            }
            if (pillarSWTxtCpnt != null) {
                buildPillarMessage("%southwest%", pillarSWTxtCpnt, pillarCoordinate.getSouthWestBlockLocation(), pillarData, replacer);
            }
            if (pillarSETxtCpnt != null) {
                buildPillarMessage("%southeast%", pillarSETxtCpnt, pillarCoordinate.getSouthEastBlockLocation(), pillarData, replacer);
            }

            player.spigot().sendMessage(replacer.create());
        }
    }
}
