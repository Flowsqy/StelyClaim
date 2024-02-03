package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandNode extends CommandTabExecutor {

    @NotNull
    String[] getTriggers();

    @NotNull
    String getName();

    @NotNull
    ResolveResult resolve(@NotNull CommandContext context);

    boolean canExecute(@NotNull CommandContext context);

    default boolean canTabComplete(@NotNull CommandContext context) {
        return canExecute(context);
    }

}
