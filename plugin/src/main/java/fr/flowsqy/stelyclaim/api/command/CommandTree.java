package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class CommandTree {

    private final CommandTree[] children;
    private final CommandPredicate predicate;

    public CommandTree(@NotNull CommandPredicate predicate) {
        children = new CommandTree[0];
        this.predicate = predicate;
    }


    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        if (context.getArgsLength() == 0) {
            return new ResolveResult(this, context.buildArgs());
        }

        for (CommandTree child : children) {
            if (!child.accept(context)) {
                continue;
            }
            context.consumeArg();
            final ResolveResult result = child.resolve(context);
            context.restoreArg();
            return result;
        }
        return new ResolveResult(this, context.buildArgs());
    }

    boolean accept(@NotNull CommandContext context) {
        return predicate.accept(context);
    }

}
