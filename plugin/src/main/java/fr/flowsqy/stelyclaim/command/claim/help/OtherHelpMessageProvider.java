package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OtherHelpMessageProvider implements HelpMessageProvider {

    private final OtherPermissionChecker permChecker;
    private final String baseMessage;
    private final String otherMessage;

    public OtherHelpMessageProvider(@NotNull OtherPermissionChecker permChecker, @Nullable String baseMessage,
                                    @Nullable String otherMessage) {
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
