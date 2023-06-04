package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

public interface CommandNode<T> extends CommandTabExecutor<T> {

    @NotNull
    String[] getTriggers();

    @NotNull
    String getTabCompletion();

    boolean canExecute(@NotNull CommandContext<T> context);

    boolean canTabComplete(@NotNull CommandContext<T> context);

}
