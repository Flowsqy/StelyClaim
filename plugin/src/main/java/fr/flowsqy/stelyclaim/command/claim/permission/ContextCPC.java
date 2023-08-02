package fr.flowsqy.stelyclaim.command.claim.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public class ContextCPC implements CommandPermissionChecker {

    private final String prefix;
    private final String suffix;

    public ContextCPC(@NotNull String prefix, @NotNull String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @NotNull
    public String buildBasePerm(@NotNull CommandContext context) {
        final String handlerId = null; // TODO Retrieve id
        return prefix + handlerId + suffix;
    }

    @Override
    public boolean checkBase(@NotNull CommandContext context) {
        return context.hasPermission(buildBasePerm(context));
    }

}
