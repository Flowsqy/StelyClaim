package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class AddOwnerSubCommand extends DomainSubCommand {

    private final static String NAME = "addowner";
    private final static String[] TRIGGERS = new String[]{NAME, "ao"};
    private final ProtocolManager protocolManager;

    public AddOwnerSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interact(@NotNull Actor actor, @NotNull World world, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer target) {
        return protocolManager.addOwner(world, actor, owner, target);
    }
}
