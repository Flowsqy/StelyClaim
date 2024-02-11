package fr.flowsqy.stelyclaim.api.command;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public interface CommandExecutor {

    void execute(@NotNull ActionContext context);

}
