package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public class BasicPC implements PermissionChecker {

    private final @NotNull String permission;

    public BasicPC(@NotNull String permission) {
        this.permission = permission;
    }

    @NotNull
    public String getBasePermission() {
        return permission;
    }

    @Override
    public boolean checkBase(@NotNull CommandContext context) {
        return context.hasPermission(permission);
    }

}
