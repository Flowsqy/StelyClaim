package fr.flowsqy.stelyclaim.command.subcommand.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.command.subcommand.ProtocolSubCommand;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class DomainSubCommand extends ProtocolSubCommand {

    public DomainSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;

        if (size == 2) {
            return interact(
                    player.getWorld(),
                    player,
                    protocolManager.getHandler("player"),
                    new PlayerOwner(player),
                    OfflinePlayerRetriever.getOfflinePlayer(args.get(1))
            );
        }
        if (size == 3) {
            return interact(
                    player.getWorld(),
                    player,
                    protocolManager.getHandler("player"),
                    new PlayerOwner(OfflinePlayerRetriever.getOfflinePlayer(args.get(1))),
                    OfflinePlayerRetriever.getOfflinePlayer(args.get(2))
            );
        }


        final boolean hasOtherPerm = player.hasPermission(getPermission() + "-other");
        messages.sendMessage(player, "help." + getName() + (hasOtherPerm ? "-other" : ""));
        return false;
    }

    protected abstract <T extends ClaimOwner> boolean interact(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer target);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        final int size = args.size();
        if (
                size == 2 ||
                        (size == 3 &&
                                (
                                        sender.getName().toLowerCase(Locale.ROOT).startsWith(args.get(2).toLowerCase(Locale.ROOT)) ||
                                                sender.hasPermission(getPermission() + "-other")
                                )
                        )
        ) {
            final String arg = args.get(size - 1).toLowerCase(Locale.ROOT);
            final Player player = (Player) sender;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        } else
            return Collections.emptyList();
    }
}
