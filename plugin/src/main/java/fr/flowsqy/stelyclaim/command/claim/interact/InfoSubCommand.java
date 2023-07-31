package fr.flowsqy.stelyclaim.command.claim.interact;

import java.util.Collection;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.interact.InfoHandler;

public class InfoSubCommand extends InteractSubCommand {
    private final ProtocolManager protocolManager;

    public InfoSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherCommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected void interactRegion(@NotNull CommandContext context) {
        protocolManager.interact(context, new InfoHandler());
    }

}
