package fr.flowsqy.stelyclaim.api.command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.actor.Actor;

public class CommandContext extends ActionContext {

    private final LinkedList<String> currentPath;
    private final String[] args;
    private int argPos;

    public CommandContext(@NotNull Actor sender, @NotNull String[] args, @Nullable Object data, int argPos) {
        super(sender, data);
        currentPath = new LinkedList<>();
        this.args = args;
        this.argPos = argPos;
    }

    public static CommandContext buildFake(@NotNull CommandContext context, @NotNull String[] args,
            @NotNull String... commandPath) {
        final CommandContext fakeContext = new CommandContext(context.getActor(), args, null, 0);
        for (String commandName : commandPath) {
            fakeContext.appendCommandName(commandName);
        }
        return context;
    }

    @NotNull
    public String[] copyArgs() {
        final String[] copy = new String[getArgsLength()];
        System.arraycopy(args, argPos, copy, 0, copy.length);
        return copy;
    }

    public int getArgsLength() {
        return args.length - argPos;
    }

    @NotNull
    public String getArg(int argNumber) {
        return args[argNumber + argPos];
    }

    public void consumeArg() {
        this.argPos++;
    }

    public void restoreArg() {
        this.argPos--;
    }

    public void appendCommandName(@NotNull String commandName) {
        currentPath.add(commandName);
    }

    public void removeLastCommandName() {
        currentPath.removeLast();
    }

    @NotNull
    public List<String> getCurrentPath() {
        return Collections.unmodifiableList(currentPath);
    }

}
