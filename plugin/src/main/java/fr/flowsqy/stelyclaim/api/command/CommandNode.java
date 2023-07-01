package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandNode<T> extends CommandTabExecutor<T> {

    @NotNull
    String[] getTriggers();

    @NotNull
    String getTabCompletion();

    boolean canExecute(@NotNull CommandContext<T> context);

    default boolean canTabComplete(@NotNull CommandContext<T> context) {
        return canExecute(context);
    }

}
