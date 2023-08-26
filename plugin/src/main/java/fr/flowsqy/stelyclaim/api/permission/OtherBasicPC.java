package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public class OtherBasicPC extends BasicPC implements OtherPermissionChecker {

    public OtherBasicPC(@NotNull String permission) {
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
