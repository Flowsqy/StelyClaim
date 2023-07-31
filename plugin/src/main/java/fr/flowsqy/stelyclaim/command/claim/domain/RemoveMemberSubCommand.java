package fr.flowsqy.stelyclaim.command.claim.domain;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;

public class RemoveMemberSubCommand extends DomainSubCommand {

    private final ProtocolManager protocolManager;

    public RemoveMemberSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherCommandPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, data, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected void interact(@NotNull CommandContext context, @NotNull OfflinePlayer target) {
        protocolManager.removeMember(context, target);
    }

}
