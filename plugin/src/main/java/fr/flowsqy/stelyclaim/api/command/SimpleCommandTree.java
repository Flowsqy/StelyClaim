package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class SimpleCommandTree implements CommandTree {

    private final CommandTree[] children;
    private final CommandNode node;

    public SimpleCommandTree(@NotNull CommandNode node) {
        children = new CommandTree[0];
        this.node = node;
    }


    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        if (context.getArgsLength() == 0) {
            throw new IllegalArgumentException("Trying to access a tree without specifying any arguments");
        }
        final ResolveResult ownResult = node.resolve(context);
        if (!ownResult.success()) {
            return ownResult;
        }
        context.consumeArg();
        if(context.getArgsLength() == 0) {
            return ownResult;
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
        return new ResolveResult(this, true, true);
    }

}
