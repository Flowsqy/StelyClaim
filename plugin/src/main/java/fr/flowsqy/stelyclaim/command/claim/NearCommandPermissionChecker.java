package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.permission.BasicCPC;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class NearCommandPermissionChecker extends BasicCPC {

    public NearCommandPermissionChecker(@NotNull String permission) {
        super(permission);
    }

    @NotNull
    public String getFullPermission() {
        return getBasePermission() + "-full";
    }

    public boolean checkFull(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(getFullPermission());
    }

}
