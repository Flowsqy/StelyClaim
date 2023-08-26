package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class DispatchCommandTabExecutor implements CommandTabExecutor {

    @NotNull
    public abstract Iterable<CommandNode> getChildren();

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getArgsLength() == 0) {
            executeSelf(context);
            return;
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        CommandNode selectedChild = null;
        nodeLoop:
        for (CommandNode child : getChildren()) {
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
        context.appendCommandName(selectedChild.getName());
        selectedChild.execute(context);
        context.removeLastCommandName();
        context.restoreArg();
    }

    public void executeSelf(@NotNull CommandContext context) {
        fallBackExecute(context);
    }

    public abstract void fallBackExecute(@NotNull CommandContext context);

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        if (context.getArgsLength() > 1) {
            CommandNode selectedChild = null;
            nodeLoop:
            for (CommandNode child : getChildren()) {
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
            context.appendCommandName(selectedChild.getName());
            return selectedChild.tabComplete(context);
        }
        final List<String> completions = new LinkedList<>();
        for (CommandNode child : getChildren()) {
            if (!child.canTabComplete(context)) {
                continue;
            }
            final String completion = child.getName();
            if (!completion.startsWith(arg)) {
                continue;
            }
            completions.add(completion);
        }
        return completions;
    }

}
