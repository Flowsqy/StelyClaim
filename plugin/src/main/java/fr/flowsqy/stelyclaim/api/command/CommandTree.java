package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public interface CommandTree {

    public ResolveResult resolve(@NotNull CommandContext context);

}
