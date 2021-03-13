package fr.flowsqy.stelyclaim.command.subcommand.domain;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.RegionSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class DomainSubCommand extends RegionSubCommand {

    public DomainSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;

        if(size == 3){
            final String regionName = args.get(1);
            final boolean ownRegion = regionName.equalsIgnoreCase(sender.getName());
            if(checkRegion(player, regionName, ownRegion, args.get(2)))
                return;
        }
        else if(size == 2){
            if(checkRegion(player, player.getName(), true, args.get(1)))
                return;
        }

        final boolean hasOtherPerm = player.hasPermission(getPermission()+"-other");
        messages.sendMessage(player, "help."+getName() + (hasOtherPerm ? "-other" : ""));
    }

    private boolean checkRegion(Player player, String regionName, boolean ownRegion, String targetName){
        if(ownRegion || player.hasPermission(getPermission()+"-other")){
            final String worldName = player.getWorld().getName();
            final RegionManager regionManager = getRegionManager(worldName);
            if(regionManager == null){
                messages.sendMessage(player, "claim.worldnothandle", "%world%", worldName);
                return true;
            }

            final ProtectedRegion region = regionManager.getRegion(regionName);
            if(region == null){
                messages.sendMessage(player, "claim.notexist" + (ownRegion ? "" : "other"), "%region%", "%world%", regionName, worldName);
                return true;
            }

            if(!modifyRegion(player, region, targetName, ownRegion, regionName))
                return true;

            if(!ownRegion){
                plugin.getMailManager().sendInfoToTarget(player, regionName, getName(), targetName);
            }

            return true;
        }
        return false;
    }

    protected abstract boolean modifyRegion(Player sender, ProtectedRegion region, String targetPlayer, boolean ownRegion, String regionName);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        final int size = args.size();
        if(
                size == 2 ||
                        (size == 3 &&
                                (
                                        sender.getName().toLowerCase(Locale.ROOT).startsWith(args.get(2).toLowerCase(Locale.ROOT)) ||
                                        sender.hasPermission(getPermission()+"-other")
                                )
                        )
        ){
            final String arg = args.get(size - 1).toLowerCase(Locale.ROOT);
            final Player player = (Player) sender;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        else
            return Collections.emptyList();
    }
}
