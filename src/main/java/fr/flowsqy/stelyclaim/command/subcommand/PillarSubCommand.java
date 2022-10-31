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

    public PillarSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, HelpSubCommand helpSubCommand) {
        super(plugin.getMessages(), name, alias, permission, console, allowedWorlds, statistic);
        this.helpSubCommand = helpSubCommand;
        this.pillarData = plugin.getPillarData();
        this.teleportSync = plugin.getTeleportSync();
    }

    @Override
    public String getHelpMessage(CommandSender sender) {
        // Nothing to send, invisible command
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if (args.size() != 2) {
            helpSubCommand.execute(sender, args, size, isPlayer);
            return false;
        }
        final PillarData pillarData = this.pillarData.get(sender.getName());
        if (pillarData == null) {
            helpSubCommand.execute(sender, args, size, isPlayer);
            return false;
        }
        final String arg = args.get(1);
        if (arg.length() != 1) {
            helpSubCommand.execute(sender, args, size, isPlayer);
            return false;
        }
        final Location loc = pillarData.getLocations().get(arg);
        if (loc == null) {
            helpSubCommand.execute(sender, args, size, isPlayer);
            return false;
        }
        if (loc.getWorld() == null) // Normally impossible
            return false;

        final Location teleportLoc = loc.clone();
        if (teleportLoc.getX() == Math.floor(teleportLoc.getX())) {
            // Correct position only for pillar loc (exclude current position)
            teleportLoc.setY(loc.getWorld().getHighestBlockYAt(loc));
            teleportLoc.add(0.5, 1, 0.5);
        }
        teleportSync.addTeleport((Player) sender, teleportLoc);
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return Collections.emptyList();
    }

}
