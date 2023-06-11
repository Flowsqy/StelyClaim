package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class DispatchCommandTabExecutor<T> implements CommandTabExecutor<T> {

    @NotNull
    public abstract Iterable<CommandNode<T>> getChildren();

    @Override
    public void execute(@NotNull CommandContext<T> context) {
        if (context.getArgsLength() == 0) {
            return;
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        CommandNode<T> selectedChild = null;
        nodeLoop:
        for (CommandNode<T> child : getChildren()) {
            if (!child.canExecute(context)) {
                continue;
            }
            for (String trigger : child.getTriggers()) {
                if (!trigger.equals(arg)) {
                    continue;
                }
                selectedChild = child;
                break nodeLoop;
            }
        }
        if (selectedChild == null) {
            fallBackExecute(context);
            return;
        }
        context.consumeArg();
        selectedChild.execute(context);
        context.restoreArg();
    }

    public abstract void fallBackExecute(@NotNull CommandContext<T> context);

    @Override
    public List<String> tabComplete(@NotNull CommandContext<T> context) {
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        if (context.getArgsLength() > 1) {
            CommandNode<T> selectedChild = null;
            nodeLoop:
            for (CommandNode<T> child : getChildren()) {
                if (!child.canTabComplete(context)) {
                    continue;
                }
                for (String trigger : child.getTriggers()) {
                    if (!trigger.equals(arg)) {
                        continue;
                    }
                    selectedChild = child;
                    break nodeLoop;
                }
            }
            if (selectedChild == null) {
                return Collections.emptyList();
            }
            context.consumeArg();
            return selectedChild.tabComplete(context);
        }
        final List<String> completions = new LinkedList<>();
        for (CommandNode<T> child : getChildren()) {
            if (!child.canTabComplete(context)) {
                continue;
            }
            final String completion = child.getTabCompletion();
            if (!completion.startsWith(arg)) {
                continue;
            }
            completions.add(completion);
        }
        return completions;
    }

}
