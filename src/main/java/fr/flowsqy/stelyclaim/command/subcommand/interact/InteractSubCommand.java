package fr.flowsqy.stelyclaim.command.subcommand.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.RegionSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class InteractSubCommand extends RegionSubCommand {

    public InteractSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if(size != 1 && size != 2){
            messages.sendMessage(sender,
                    "help."
                            + getName()
                            + (sender.hasPermission(getPermission()+"-other") ? "-other" : "")
            );
            return;
        }
        final Player player = (Player) sender;

        final String regionName;
        final boolean ownRegion;
        if(size == 1){
            regionName = player.getName();
            ownRegion = true;
        }
        else{
            regionName = args.get(1);
            ownRegion = regionName.equalsIgnoreCase(player.getName());
        }

        if(!ownRegion && !player.hasPermission(getPermission()+"-other")){
            messages.sendMessage(player, "help."+getPermission());
            return;
        }

        final World world = player.getWorld();

        final RegionManager regionManager = getRegionManager(world);
        if(regionManager == null){
            messages.sendMessage(player,
                    "claim.worldnothandle",
                    "%world%", world.getName());
            return;
        }

        final ProtectedRegion region = regionManager.getRegion(regionName);
        if(region == null){
            messages.sendMessage(player, "claim.notexist" + (ownRegion ? "" : "other"), "%region%", regionName);
            return;
        }
        if(region.getType() == RegionType.GLOBAL){
            messages.sendMessage(player, "claim.global");
            return;
        }

        interactRegion(player, regionManager, region, ownRegion, regionName);
    }

    protected abstract void interactRegion(Player player, RegionManager regionManager, ProtectedRegion region, boolean ownRegion, String regionName);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if(sender.hasPermission(getPermission()+"-other") && args.size() == 2){
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .filter(((Player) sender)::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        else
            return Collections.emptyList();
    }
}
