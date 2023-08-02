package fr.flowsqy.stelyclaim.command.claim;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.BasicPC;

public class NearCommandPermissionChecker extends BasicPC {

    public NearCommandPermissionChecker(@NotNull String permission) {
        super(permission);
    }

    @NotNull
    public String getFullPermission() {
        return getBasePermission() + "-full";
    }

    public boolean checkFull(@NotNull CommandContext context) {
        return context.hasPermission(getFullPermission());
    }

}
