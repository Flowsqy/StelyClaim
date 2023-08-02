package fr.flowsqy.stelyclaim.command.claim.selection;

import java.util.Collection;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.selection.SelectionProtocol;

public class RedefineSubCommand extends SelectionSubCommand {

    public RedefineSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    protected void interactRegion(@NotNull CommandContext context) {
        new SelectionProtocol().process(context);
    }

}
