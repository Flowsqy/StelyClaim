package fr.flowsqy.stelyclaim.command.subcommand.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class AddOwnerSubCommand extends DomainSubCommand {
    public AddOwnerSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    protected <T extends ClaimOwner> boolean interact(World world, Player sender, ClaimHandler<T> handler, T owner, OfflinePlayer target) {
        return protocolManager.addOwner(world, sender, handler, owner, target);
    }

}
