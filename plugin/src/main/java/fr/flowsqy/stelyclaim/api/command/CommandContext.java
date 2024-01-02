package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public class CommandContext {

    private final PermissionCache permissionCache;
    private final String[] args;
    private int argPos;

    public CommandContext(@NotNull String[] args, @NotNull PermissionCache permissionCache) {
        this.permissionCache = permissionCache;
        this.args = args;
        this.argPos = 0;
    }

    @NotNull
    public PermissionCache getPermissionCache() {
        return permissionCache;
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
    public CommandArgs buildArgs() {
        final String[] path = new String[argPos];
        System.arraycopy(args, 0, path, 0, path.length);
        final String[] commandArgs = new String[args.length - argPos];
        System.arraycopy(args, 0, commandArgs, argPos, commandArgs.length);
        return new CommandArgs(path, commandArgs);
    }

}
