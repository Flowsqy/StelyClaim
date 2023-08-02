package fr.flowsqy.stelyclaim.command.claim.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public class BasicCPC implements CommandPermissionChecker {

    private final @NotNull String permission;

    public BasicCPC(@NotNull String permission) {
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
