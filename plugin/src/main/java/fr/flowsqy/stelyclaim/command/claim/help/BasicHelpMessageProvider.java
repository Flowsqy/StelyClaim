package fr.flowsqy.stelyclaim.command.claim.help;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.permission.CommandPermissionChecker;

public class BasicHelpMessageProvider implements HelpMessageProvider {

    private final CommandPermissionChecker permChecker;
    private final String message;

    public BasicHelpMessageProvider(@NotNull CommandPermissionChecker permChecker, @NotNull String message) {
        this.permChecker = permChecker;
        this.message = message;
    }

    @Override
    public String get(@NotNull CommandContext context) {
        return permChecker.checkBase(context) ? message : null;
    }

}
