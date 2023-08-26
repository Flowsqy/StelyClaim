package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public class OtherContextPC extends ContextPC implements OtherPermissionChecker {

    public OtherContextPC(@NotNull String prefix, @NotNull String suffix) {
        super(prefix, suffix);
    }

    @NotNull
    public String buildOtherPerm(@NotNull ActionContext context) {
        return buildBasePerm(context) + "-other";
    }

    @Override
    public boolean checkOther(@NotNull ActionContext context) {
        return context.hasPermission(buildOtherPerm(context));
    }

}
