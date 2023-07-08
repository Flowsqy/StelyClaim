package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RemoveSubCommand extends InteractSubCommand {

    private final ProtocolManager protocolManager;

    public RemoveSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull CommandPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(name, triggers, plugin, worlds, data, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected void interactRegion(@NotNull CommandContext<ClaimContext> context) {
        protocolManager.remove(context);
    }

}
