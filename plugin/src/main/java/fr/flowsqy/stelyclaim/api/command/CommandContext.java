package fr.flowsqy.stelyclaim.api.command;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandContext<T> extends ActionContext<T> {

    private final Map<String, Boolean> permissions;
    private final String[] args;
    private int argPos;

    public CommandContext(@NotNull Actor sender, @NotNull String[] args, @NotNull T data, int argPos) {
        super(sender, data);
        permissions = new HashMap<>();
        this.args = args;
        this.argPos = argPos;
    }

    public boolean hasPermission(@NotNull String permission) {
        return permissions.computeIfAbsent(permission, perm -> getActor().getBukkit().hasPermission(perm));
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

}
