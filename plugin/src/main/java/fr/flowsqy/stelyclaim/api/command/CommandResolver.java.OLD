package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiPredicate;

public class CommandResolver {

    private final DispatchCommandTabExecutor root;

    public CommandResolver(@NotNull DispatchCommandTabExecutor root) {
        this.root = root;
    }

    @NotNull
    public Result resolve(@NotNull CommandContext context,
                          @NotNull BiPredicate<CommandNode, CommandContext> explorePredicate) {
        if (context.getArgsLength() == 0) {
            return new DispatcherResult(root);
        }
        DispatchCommandTabExecutor node = root;
        CommandNode selectedChild = null;
        Iterator<CommandNode> commandItr = root.getChildren().iterator();
        for (CommandNode child = null; commandItr.hasNext(); ) {
            child = commandItr.next();
            if (!explorePredicate.test(child, context) || !child.getName().equals(context.getArg(0))) {
                continue;
            }
            if (child instanceof DispatchCommandTabExecutor dcte) {
                commandItr = dcte.getChildren().iterator();
                node = dcte;
                context.consumeArg();
                context.appendCommandName(child.getName());
                if (context.getArgsLength() > 0) {
                    continue;
                }
                break;
            }

            selectedChild = child;
            break;
        }

        if (selectedChild != null) {
            context.consumeArg();
            context.appendCommandName(selectedChild.getName());
            return new CommandNodeResult(selectedChild);
        }
        return new DispatcherResult(node);
    }

    public interface Result {

        boolean isDispatcher();

        @NotNull
        DispatchCommandTabExecutor asDispatcher();

        @NotNull
        CommandNode asNode();

    }

    public static class DispatcherResult implements Result {

        @NotNull
        private final DispatchCommandTabExecutor dispatcher;

        public DispatcherResult(@NotNull DispatchCommandTabExecutor dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public boolean isDispatcher() {
            return true;
        }

        @Override
        public @NotNull DispatchCommandTabExecutor asDispatcher() {
            return dispatcher;
        }

        @Override
        public @NotNull CommandNode asNode() {
            throw new UnsupportedOperationException();
        }

    }

    public static class CommandNodeResult implements Result {

        @NotNull
        private final CommandNode commandNode;

        public CommandNodeResult(@NotNull CommandNode commandNode) {
            this.commandNode = commandNode;
        }

        @Override
        public boolean isDispatcher() {
            return false;
        }

        @Override
        public @NotNull DispatchCommandTabExecutor asDispatcher() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull CommandNode asNode() {
            return commandNode;
        }

    }

}
