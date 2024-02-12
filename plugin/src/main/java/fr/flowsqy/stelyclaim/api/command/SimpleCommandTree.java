package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class SimpleCommandTree implements CommandTree {

    private final CommandNode node;
    private final GroupCommandTree children;

    public SimpleCommandTree(@NotNull CommandNode node, @NotNull GroupCommandTree children) {
        this.node = node;
        this.children = children;
    }

    @Override
    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        if (context.getArgsLength() == 0) {
            throw new IllegalArgumentException("Trying to access a tree without specifying any arguments");
        }
        final ResolveResult ownResult = node.resolve(context);
        if (ownResult.node().isEmpty()) {
            return ownResult;
        }
        context.consumeArg();
        if(context.getArgsLength() == 0) {
            return ownResult;
        }
        final ResolveResult childResult = children.resolve(context);
        if(childResult.node().isPresent()){
            return childResult;
        }
        return ownResult;
    }

}
