package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class OtherHelpMessageProvider implements Function<CommandContext<ClaimContext>, String> {

    private final OtherCommandPermissionChecker permChecker;
    private final String baseMessage;
    private final String otherMessage;

    public OtherHelpMessageProvider(@NotNull OtherCommandPermissionChecker permChecker, @NotNull String baseMessage, @NotNull String otherMessage) {
        this.permChecker = permChecker;
        this.baseMessage = baseMessage;
        this.otherMessage = otherMessage;
    }

    @Override
    public String apply(@NotNull CommandContext<ClaimContext> context) {
        if (permChecker.checkOther(context)) {
            return otherMessage;
        }
        return permChecker.checkBase(context) ? baseMessage : null;
    }

}
