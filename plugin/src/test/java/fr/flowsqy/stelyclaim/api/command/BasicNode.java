package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

class BasicNode implements CommandNode {

    private final String name;
    private final String permission;

    public BasicNode(@NotNull String name, @NotNull String permission) {
        this.name = name;
        this.permission = permission;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
    }

    @Override
    @Nullable
    public List<String> tabComplete(@NotNull CommandContext context) {
        return null;
    }

    @Override
    @NotNull
    public ResolveResult resolve(@NotNull CommandContext context) {
        final String arg = context.getArg(0);
        if (!name.equalsIgnoreCase(arg)) {
            return new ResolveResult(Optional.empty(), false);
        } 
        if (!context.getPermissionCache().hasPermission(permission)) {
            return new ResolveResult(Optional.empty(), true);
        }
        return new ResolveResult(Optional.of(this), true);
    }


}

