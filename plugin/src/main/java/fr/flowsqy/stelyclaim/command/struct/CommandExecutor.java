package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

public interface CommandExecutor<T> {

    void execute(@NotNull CommandContext<T> context);


}
