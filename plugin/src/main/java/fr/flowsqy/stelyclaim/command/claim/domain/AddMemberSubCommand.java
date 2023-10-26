package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimInteractHandler;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.HandlerContext;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.message.DomainMessage;
import fr.flowsqy.stelyclaim.message.FallbackFormattedMessages;
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
    }

    @Override
    protected void sendMessage(@NotNull CommandContext context) {
        super.sendMessage(context);
        final ClaimInteractHandler<?> claimInteractHandler = context.getCustomData(HandlerContext.class).getHandler().getClaimInteractHandler();
        final FormattedMessages specificMessage = claimInteractHandler == null ? null : claimInteractHandler.getMessages();
        final FormattedMessages usedMessages = specificMessage == null ? plugin.getMessages() : new FallbackFormattedMessages(plugin.getMessages(), specificMessage);
        new DomainMessage().sendMessage(context, usedMessages, "member", "already", "add");
    }

}
