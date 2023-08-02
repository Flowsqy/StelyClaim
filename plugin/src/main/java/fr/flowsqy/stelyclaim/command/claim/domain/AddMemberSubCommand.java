package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class AddMemberSubCommand extends DomainSubCommand {

    private final ProtocolManager protocolManager;

    public AddMemberSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull OtherPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, data, helpMessage);
        protocolManager = plugin.getProtocolManager();
    }

    @Override
    protected void interact(@NotNull CommandContext context, @NotNull OfflinePlayer target) {
        protocolManager.addMember(context, target);
    }

}
