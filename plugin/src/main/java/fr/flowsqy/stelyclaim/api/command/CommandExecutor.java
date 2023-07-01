package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandExecutor<T> {

    void execute(@NotNull CommandContext<T> context);


}
