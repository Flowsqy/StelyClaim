package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.interact.InteractSubCommand;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import org.jetbrains.annotations.NotNull;

public abstract class SelectionSubCommand extends InteractSubCommand {

    public SelectionSubCommand(@NotNull String name, @NotNull String[] triggers) {
        super(name, triggers);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.getSender().isPlayer() && super.canExecute(context);
    }

}
