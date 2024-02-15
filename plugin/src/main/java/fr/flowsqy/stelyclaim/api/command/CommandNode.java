package fr.flowsqy.stelyclaim.api.command;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public interface CommandNode extends CommandTabExecutor {

    @NotNull
    ResolveResult resolve(@NotNull CommandContext context);

    @NotNull
    default Optional<String> getNameAsChild(@NotNull CommandContext context) {
        return Optional.empty();
    }

}
