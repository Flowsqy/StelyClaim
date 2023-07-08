package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RemoveMemberSubCommand extends DomainSubCommand {

    private final ProtocolManager protocolManager;

    public RemoveMemberSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull CommandPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(name, triggers, plugin, worlds, data, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected void interact(@NotNull CommandContext<ClaimContext> context, @NotNull OfflinePlayer target) {
        protocolManager.removeMember(context, target);
    }

}
