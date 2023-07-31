package fr.flowsqy.stelyclaim.command.claim.selection;

import java.util.Collection;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.interact.InteractSubCommand;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;

public abstract class SelectionSubCommand extends InteractSubCommand {

    public SelectionSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
            @NotNull OtherCommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getActor().isPlayer() && super.canExecute(context);
    }

}
