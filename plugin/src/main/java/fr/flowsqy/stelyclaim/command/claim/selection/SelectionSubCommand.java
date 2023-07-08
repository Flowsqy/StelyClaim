package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.command.claim.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.interact.InteractSubCommand;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class SelectionSubCommand extends InteractSubCommand {

    public SelectionSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull CommandPermissionChecker data, @NotNull HelpMessage helpMessage) {
        super(name, triggers, plugin, worlds, data, helpMessage);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContext> context) {
        return context.getActor().isPlayer() && super.canExecute(context);
    }

}
