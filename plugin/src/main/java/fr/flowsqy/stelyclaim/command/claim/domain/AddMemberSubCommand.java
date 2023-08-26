package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class AddMemberSubCommand extends DomainSubCommand {

    public AddMemberSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull OtherPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, data, helpMessage);
    }

    @Override
    protected void interact(@NotNull CommandContext context, @NotNull OfflinePlayer target) {
        final InteractProtocol protocol = new InteractProtocol(new DomainProtocol(DomainProtocol.Protocol.ADD_MEMBER, target), getPermChecker());
        protocol.process(context);
        final ActionResult result = context.getResult();

    }

}
