package fr.flowsqy.stelyclaim.command.claim.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public class OtherBasicCPC extends BasicCPC implements OtherCommandPermissionChecker {

    public OtherBasicCPC(@NotNull String permission) {
        super(permission);
    }

    @NotNull
    public String getOtherPermission() {
        return getBasePermission() + "-other";
    }

    @Override
    public boolean checkOther(@NotNull CommandContext context) {
        return context.hasPermission(getOtherPermission());
    }
}
