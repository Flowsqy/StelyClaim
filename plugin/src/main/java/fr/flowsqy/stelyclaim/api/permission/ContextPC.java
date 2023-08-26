package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class ContextPC implements PermissionChecker {

    private final String prefix;
    private final String suffix;

    public ContextPC(@NotNull String prefix, @NotNull String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @NotNull
    public String buildBasePerm(@NotNull ActionContext context) {
        final String handlerId = ((ClaimContext)context.getCustomData()).getOwnerContext().getHandler().getId(); // TODO Retrieve id
        return prefix + handlerId + suffix;
    }

    @Override
    public boolean checkBase(@NotNull CommandContext context) {
        return context.hasPermission(buildBasePerm(context));
    }

}
