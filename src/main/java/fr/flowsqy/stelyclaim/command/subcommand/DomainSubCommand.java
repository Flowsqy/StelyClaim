package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class DomainSubCommand extends RegionSubCommand {

    public DomainSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console) {
        super(plugin, name, alias, permission, stats, console);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;

        if(size == 3){
            final String claimName = args.get(1);
            final boolean ownRegion = claimName.equalsIgnoreCase(sender.getName());
            if(checkRegion(player, claimName, ownRegion, args.get(2)))
                return;
        }
        else if(size == 2){
            if(checkRegion(player, player.getName(), true, args.get(1)))
                return;
        }

        final boolean hasOtherPerm = player.hasPermission(getPermission()+"-other");
        messages.sendMessage(player, "help."+getName() + (hasOtherPerm ? "-other" : ""));
    }

    private boolean checkRegion(Player player, String claimName, boolean ownRegion, String targetName){
        if(ownRegion || player.hasPermission(getPermission()+"-other")){
            final String worldName = player.getWorld().getName();
            final RegionManager regionManager = getRegionContainer(worldName);
            if(regionManager == null){
                messages.sendMessage(player, "claim.worldnothandle", "%world%", worldName);
                return true;
            }

            final ProtectedRegion region = regionManager.getRegion(claimName);
            if(region == null){
                messages.sendMessage(player, "claim.notexist" + (ownRegion ? "" : "other"), "%region%", "%world%", claimName, worldName);
                return true;
            }

            if(!modifyRegion(player, region, targetName, ownRegion))
                return true;

            if(!ownRegion){
                final Player targetPlayer = Bukkit.getPlayer(claimName);
                if(targetPlayer != null && player.canSee(targetPlayer)){
                    messages.sendMessage(
                            targetPlayer,
                            "claim."+getName()+"-target",
                            "%sender%", "%target%",
                            player.getName(), targetName);
                }
                else{
                    plugin.getMailManager().sendMail(
                            player,
                            targetPlayer == null ? claimName : targetPlayer.getName(),
                            getName(),
                            targetName
                    );
                }
            }

            return true;
        }
        return false;
    }

    protected abstract boolean modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        // TODO Tab completion
        return Collections.emptyList();
    }
}
