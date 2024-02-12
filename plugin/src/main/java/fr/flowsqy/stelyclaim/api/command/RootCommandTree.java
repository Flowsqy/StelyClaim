package fr.flowsqy.stelyclaim.api.command;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class RootCommandTree {

    private final CommandNode node;
    private final GroupCommandTree children;

    public RootCommandTree(@NotNull CommandNode node, @NotNull GroupCommandTree children) {
        this.node = node;
        this.children = children;
    }

    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        // Skip current node check as we assume be directly as this step (and not one step before)
        // Due to this behavior, this class does not implement SimpleCommandTree
        if (context.getArgsLength() == 0) {
            return new ResolveResult(Optional.of(node), true);
        }
        final ResolveResult childResult = children.resolve(context);
        if(childResult.node().isPresent()){
            return childResult;
        }
        return new ResolveResult(Optional.of(node), true);
    }
    
}

