package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class OtherBasicCPC extends BasicCPC implements OtherCommandPermissionChecker {

    public OtherBasicCPC(@NotNull String permission) {
        super(permission);
    }

    @NotNull
    public String getOtherPermission() {
        return getBasePermission() + "-other";
    }

    @Override
    public boolean checkOther(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(getOtherPermission());
    }
}
