package fr.flowsqy.stelyclaim.command.struct;

import fr.flowsqy.stelyclaim.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {

    private final CommandSender sender;
    private final Map<String, Boolean> permissions;
    private final String[] args;
    private int argPos;

    public CommandContext(@NotNull CommandSender sender, @NotNull String[] args, int argPos) {
        this.sender = sender;
        permissions = new HashMap<>();
        this.args = args;
        this.argPos = argPos;
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    public boolean hasPermission(@NotNull String permission) {
        return permissions.computeIfAbsent(permission, perm -> sender.getBukkit().hasPermission(perm));
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
