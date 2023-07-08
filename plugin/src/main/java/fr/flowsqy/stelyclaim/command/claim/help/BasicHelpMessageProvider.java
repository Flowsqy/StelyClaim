package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.permission.CommandPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BasicHelpMessageProvider implements Function<CommandContext<ClaimContext>, String> {

    private final CommandPermissionChecker permChecker;
    private final String message;

    public BasicHelpMessageProvider(@NotNull CommandPermissionChecker permChecker, @NotNull String message) {
        this.permChecker = permChecker;
        this.message = message;
    }

    @Override
    public String apply(@NotNull CommandContext<ClaimContext> context) {
        return permChecker.checkBase(context) ? message : null;
    }

}
