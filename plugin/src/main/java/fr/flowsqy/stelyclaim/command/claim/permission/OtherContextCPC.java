package fr.flowsqy.stelyclaim.command.claim.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class OtherContextCPC extends ContextCPC implements OtherCommandPermissionChecker {

    public OtherContextCPC(@NotNull String prefix, @NotNull String suffix) {
        super(prefix, suffix);
    }

    @NotNull
    public String buildOtherPerm(@NotNull CommandContext<ClaimContext> context) {
        return buildBasePerm(context) + "-other";
    }

    @Override
    public boolean checkOther(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(buildOtherPerm(context));
    }

}
