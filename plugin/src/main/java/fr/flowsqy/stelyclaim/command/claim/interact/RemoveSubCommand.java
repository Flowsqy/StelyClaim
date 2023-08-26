package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.interact.InteractProtocol;
import fr.flowsqy.stelyclaim.protocol.interact.RemoveHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class RemoveSubCommand extends InteractSubCommand {

    public RemoveSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
                            @NotNull OtherPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    protected void interactRegion(@NotNull CommandContext context) {
        final RemoveHandler removeHandler = new RemoveHandler();
        final InteractProtocol protocol = new InteractProtocol(removeHandler, getPermChecker());
        protocol.process(context);
    }

}
