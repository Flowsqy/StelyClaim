package fr.flowsqy.stelyclaim.api.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public class OtherContextPC extends ContextPC implements OtherPermissionChecker {

    public OtherContextPC(@NotNull String prefix, @NotNull String suffix) {
        super(prefix, suffix);
    }

    @NotNull
    public String buildOtherPerm(@NotNull CommandContext context) {
        return buildBasePerm(context) + "-other";
    }

    @Override
    public boolean checkOther(@NotNull CommandContext context) {
        return context.hasPermission(buildOtherPerm(context));
    }

}
