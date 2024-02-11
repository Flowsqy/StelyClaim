package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.PermissionChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicHelpMessageProvider implements HelpMessageProvider {

    private final PermissionChecker permChecker;
    private final String message;

    public BasicHelpMessageProvider(@NotNull PermissionChecker permChecker, @Nullable String message) {
        this.permChecker = permChecker;
        this.message = message;
    }

    @Override
    public String get(@NotNull CommandContext context) {
        return permChecker.checkBase(context) ? message : null;
    }

}
