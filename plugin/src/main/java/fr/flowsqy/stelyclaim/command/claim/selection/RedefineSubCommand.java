package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RedefineSubCommand extends SelectionSubCommand {

    private final static String NAME = "redefine";
    private final static String[] TRIGGERS = new String[]{NAME, "rd"};
    private final ProtocolManager protocolManager;

    public RedefineSubCommand(@NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds) {
        super(NAME, TRIGGERS, plugin, worlds);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return protocolManager.define(world, actor, owner);
    }

}
