package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.OtherCommandPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.protocol.selection.SelectionProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RedefineSubCommand extends SelectionSubCommand {

    public RedefineSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull OtherCommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    protected void interactRegion(@NotNull CommandContext<ClaimContext> context) {
        new SelectionProtocol().process(context);
    }

}
