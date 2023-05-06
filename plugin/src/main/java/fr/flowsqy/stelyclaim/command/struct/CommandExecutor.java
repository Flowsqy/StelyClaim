package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

public interface CommandExecutor {

    void execute(@NotNull CommandContext context);


}
