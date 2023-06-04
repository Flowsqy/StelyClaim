package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class AddMemberSubCommand extends DomainSubCommand {

    private final static String NAME = "addmember";
    private final static String[] TRIGGERS = new String[]{NAME, "am"};
    private final ProtocolManager protocolManager;

    public AddMemberSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS);
        this.protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interact(@NotNull Actor actor, @NotNull World world, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer target) {
        return protocolManager.addMember(world, actor, owner, target);
    }

}
