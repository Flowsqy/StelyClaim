package fr.flowsqy.stelyclaim.command.subcommand;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.util.PillarData;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PillarSubCommand extends SubCommand {

    private final HelpSubCommand helpSubCommand;
    private final Map<String, PillarData> pillarData;
    private final TeleportSync teleportSync;

    public PillarSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean stats, boolean console, HelpSubCommand helpSubCommand) {
        super(plugin, name, alias, permission, stats, console);
        this.helpSubCommand = helpSubCommand;
        this.pillarData = plugin.getPillarData();
        this.teleportSync = plugin.getTeleportSync();
    }

    @Override
    public void execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if(args.size() != 2){
            helpSubCommand.execute(sender, args, size, isPlayer);
            return;
        }
        final PillarData pillarData = this.pillarData.get(sender.getName());
        if(pillarData == null){
            helpSubCommand.execute(sender, args, size, isPlayer);
            return;
        }
        final String arg = args.get(1);
        if(arg.length() != 1){
            helpSubCommand.execute(sender, args, size, isPlayer);
            return;
        }
        final Location loc = pillarData.getLocations().get(arg);
        if(loc == null){
            helpSubCommand.execute(sender, args, size, isPlayer);
            return;
        }
        teleportSync.addTeleport((Player) sender, loc);
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return Collections.emptyList();
    }
    
}
