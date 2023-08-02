package fr.flowsqy.stelyclaim.command.claim.help;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;

public class OtherHelpMessageProvider implements HelpMessageProvider {

    private final OtherPermissionChecker permChecker;
    private final String baseMessage;
    private final String otherMessage;

    public OtherHelpMessageProvider(@NotNull OtherPermissionChecker permChecker, @NotNull String baseMessage,
            @NotNull String otherMessage) {
        this.permChecker = permChecker;
        this.baseMessage = baseMessage;
        this.otherMessage = otherMessage;
    }

    @Override
    public String get(@NotNull CommandContext context) {
        if (permChecker.checkOther(context)) {
            return otherMessage;
        }
        return permChecker.checkBase(context) ? baseMessage : null;
    }

}
