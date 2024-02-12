package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class RootCommandTree {

    private final CommandTree[] children;
    private final CommandNode node;

    public RootCommandTree(@NotNull CommandNode node, @NotNull CommandTree[] children) {
        this.node = node;
        this.children = children;
    }

    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        // Skip current node check as we assume be directly as this step (and not one step before)
        // Due to this behavior, this class does not implement SimpleCommandTree
        if (context.getArgsLength() == 0) {
            return new ResolveResult(node, true, true);
        }
        for (CommandTree child : children) {
            final ResolveResult childResult = child.resolve(context);
            if(!childResult.found()) {
                continue;
            }
            if (!childResult.success()) {
                break;
            }    
            return childResult;
        }
        return new ResolveResult(node, true, true);
    }
    
}

