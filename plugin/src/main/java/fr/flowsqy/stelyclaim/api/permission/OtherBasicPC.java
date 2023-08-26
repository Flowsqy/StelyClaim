package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
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
    public boolean checkOther(@NotNull ActionContext context) {
        return context.hasPermission(getOtherPermission());
    }
}
