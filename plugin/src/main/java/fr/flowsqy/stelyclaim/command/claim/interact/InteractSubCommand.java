package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.command.claim.ProtocolSubCommand;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class InteractSubCommand extends ProtocolSubCommand {

    public InteractSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if (size != 1 && size != 2) {
            messages.sendMessage(sender,
                    "help."
                            + getName()
                            + (sender.hasPermission(getPermission() + "-other") ? "-other" : "")
            );
            return false;
        }
        final Player player = (Player) sender;
        final OfflinePlayer targetPlayer = size == 1 ? player : OfflinePlayerRetriever.getOfflinePlayer(args.get(1));

        return interactRegion(player, protocolManager.getHandler("player"), new PlayerOwner(targetPlayer));
    }

    protected abstract <T extends ClaimOwner> boolean interactRegion(Player sender, ClaimHandler<T> handler, T owner);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (sender.hasPermission(getPermission() + "-other") && args.size() == 2) {
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .filter(((Player) sender)::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        } else
            return Collections.emptyList();
    }
}
