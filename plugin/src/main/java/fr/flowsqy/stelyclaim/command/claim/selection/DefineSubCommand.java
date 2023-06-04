package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class DefineSubCommand extends SelectionSubCommand {

    private final static String NAME = "define";
    private final static String[] TRIGGERS = new String[]{NAME, "d"};
    private final ProtocolManager protocolManager;

    public DefineSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return protocolManager.define(world, actor, owner);
    }

}
