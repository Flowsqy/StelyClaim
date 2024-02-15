package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.api.actor.Actor;

public class CommandContext {

    private final Actor actor;
    private final PermissionCache permissionCache;
    private final ActionType type;
    private final String[] args;
    private int argPos;
    private Object data;

    public CommandContext(@NotNull Actor actor, @NotNull String[] args, @NotNull PermissionCache permissionCache, @NotNull ActionType type, @Nullable Object data) {
        this.actor = actor;
        this.permissionCache = permissionCache;
        this.type = type;
        this.args = args;
        this.argPos = 0;
        this.data = data;
    }

    @NotNull
    public Actor getActor() {
        return actor;
    }

    @NotNull
    public PermissionCache getPermissionCache() {
        return permissionCache;
    }

    @NotNull
    public ActionType getType() {
        return type;
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

    @NotNull
    public String[] getRawArgs() {
        return args;
    }

    @Nullable
    public Object getData() {
        return data;
    }
     
    @NotNull
    public <T> T getCustomData(@NotNull Class<T> type) {
        return type.cast(data);
    }

    public void setCustomData(@Nullable Object data) {
        this.data = data;
    }

    public enum ActionType {
        TAB_COMPLETE,
        EXECUTE,
        UNKNOWN,
        OTHER
    }

}
