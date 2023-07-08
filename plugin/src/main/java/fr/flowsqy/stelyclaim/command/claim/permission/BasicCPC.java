package fr.flowsqy.stelyclaim.command.claim.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class BasicCPC implements CommandPermissionChecker {

    private final String permission;

    public BasicCPC(@NotNull String permission) {
        this.permission = permission;
    }

    @NotNull
    public String getBasePermission() {
        return permission;
    }

    @Override
    public boolean checkBase(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(permission);
    }

}
