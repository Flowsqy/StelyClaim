package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.context.DomainContext;
import fr.flowsqy.stelyclaim.protocol.domain.DomainProtocol;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class AddMemberSubCommand extends DomainSubCommand {

    public AddMemberSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull OtherPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, data, helpMessage);
    }

    @Override
    protected void interact(@NotNull CommandContext context, @NotNull OfflinePlayer target) {
        final InteractProtocol protocol = new InteractProtocol(new DomainProtocol(DomainProtocol.Protocol.ADD_MEMBER, target), getPermChecker());
        protocol.process(context);

        context.getActor().getBukkit().sendMessage(getMessage(context.getResult().code()).apply(context));
    }

    private Function<ActionContext, String> getMessage(int code) {

        // Generalize (specific to handler)

        if (code == InteractProtocol.CANT_OTHER) {
            return c -> "Can't other";
        }
        if (code == InteractProtocol.WORLD_NOT_HANDLED) {
            return c -> "World not handled";
        }
        if (code == InteractProtocol.REGION_NOT_EXIST) {
            return c -> {
                final DomainContext cc = c.getCustomData(DomainContext.class);
                return cc.getOwnerContext().isActorOwnTheClaim() ? "Don't have region" : ("%region% does not have region".replace("%region%", cc.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName()));
            };
        }
        if (code == InteractProtocol.TRY_INTERACT_GLOBAL) {
            return c -> "Try interact global";
        }
        if (code == DomainProtocol.CANT_MODIFY) {
            return c -> {
                final DomainContext cc = c.getCustomData(DomainContext.class);
                return cc.getOwnerContext().isActorOwnTheClaim() ? "You can't add " + cc.getTarget().orElseThrow().getName() + ", Already in" : ("%region% already has ....".replace("%region%", cc.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName()));
            };
        }
        if (code == DomainProtocol.MODIFY) {
            return c -> {
                final DomainContext cc = c.getCustomData(DomainContext.class);
                return cc.getOwnerContext().isActorOwnTheClaim() ? "You added " + cc.getTarget().orElseThrow().getName() + " in your region" : ("%region% has a new member : ...".replace("%region%", cc.getOwnerContext().getLazyHandledOwner().toHandledOwner().owner().getName()));
            };
        }
        return c -> {throw new RuntimeException();};
    }

}
