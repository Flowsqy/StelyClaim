package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandPredicate {

    boolean accept(@NotNull CommandContext context);

}
