package fr.flowsqy.stelyclaim.api.command;

import java.util.List;
import java.util.Collections;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class GroupCommandTree implements CommandTree {

    public static final GroupCommandTree EMPTY = new GroupCommandTree(Collections.emptyList());

    private final List<CommandTree> children;

    public GroupCommandTree(@NotNull List<CommandTree> children) {
        this.children = children;
    }

    @Override
    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        for (CommandTree child : children) {
            final ResolveResult childResult = child.resolve(context);
            if(!childResult.found()) {
                continue;
            }
            if (childResult.node().isEmpty()) {
                break;
            }    
            return childResult;
        }
        return new ResolveResult(Optional.empty(), false);
    }

    @NotNull
    public List<CommandTree> getChildren() {
        return children;
    }

}

