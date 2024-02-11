package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandNode extends CommandTabExecutor {

    @NotNull
    ResolveResult resolve(@NotNull CommandContext context);

}
