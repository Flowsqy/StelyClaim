package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class SimpleCommandTree implements CommandTree {

    private final CommandTree[] children;

    public SimpleCommandTree() {
        children = new CommandTree[0];
    }


    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        if (context.getArgsLength() == 0) {
            return new ResolveResult(this, context.buildArgs());
        }

        for (CommandTree child : children) {
            context.consumeArg();
            final ResolveResult result = child.resolve(context);
            context.restoreArg();
            if(result.success()) {
                continue;
            }
            return result;
        }
        return new ResolveResult(this, context.buildArgs());
    }

}
