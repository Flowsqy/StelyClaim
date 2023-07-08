package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class ContextCPC implements CommandPermissionChecker {

    private final String prefix;
    private final String suffix;

    public ContextCPC(@NotNull String prefix, @NotNull String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @NotNull
    public String buildBasePerm(@NotNull CommandContext<ClaimContext> context) {
        return prefix + context.getCustomData().orElseThrow().getOwnerContext().getHandler().getId() + suffix;
    }

    @Override
    public boolean checkBase(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(buildBasePerm(context));
    }

}
