package fr.flowsqy.stelyclaim.api.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class ChildrenTabComplete {

    private final List<CommandTree> children;

    public ChildrenTabComplete(@NotNull List<CommandTree> children) {
        this.children = children;
    }

    @NotNull
    public List<String> tabComplete(@NotNull CommandContext context) {
        final List<String> completions = new LinkedList<>();
        final String arg = context.getArg(0);
        for (CommandTree child : children) {
            if (child instanceof GroupCommandTree group) {
                final ChildrenTabComplete sub = new ChildrenTabComplete(group.getChildren());
                completions.addAll(sub.tabComplete(context));
                continue;
            }
            if (child instanceof SimpleCommandTree tree) {
                final Optional<String> optName = tree.getNode().getNameAsChild(context);
                if (optName.isEmpty()) {
                    continue;
                }
                final String name = optName.get();
                if(name.startsWith(arg)) {
                    completions.add(name);
                }
                continue;
            }
        }
        return completions;
    }

}

