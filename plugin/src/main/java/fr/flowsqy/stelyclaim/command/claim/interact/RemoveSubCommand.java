package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.claim.ClaimSubCommandData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RemoveSubCommand extends InteractSubCommand {

    private final ProtocolManager protocolManager;

    public RemoveSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull ClaimSubCommandData data, @NotNull HelpMessage helpMessage) {
        super(name, triggers, plugin, worlds, data, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner) {
        return protocolManager.remove(world, actor, owner);
    }

}
