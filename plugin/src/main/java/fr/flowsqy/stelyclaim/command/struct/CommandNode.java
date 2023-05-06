package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

public interface CommandNode extends CommandTabExecutor {

    @NotNull
    String[] getTriggers();

    @NotNull
    String getTabCompletion();

    boolean canExecute(@NotNull CommandContext context);

    boolean canTabComplete(@NotNull CommandContext context);

}
