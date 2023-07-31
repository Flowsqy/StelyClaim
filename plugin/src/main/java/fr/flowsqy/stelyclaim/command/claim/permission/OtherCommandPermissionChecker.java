package fr.flowsqy.stelyclaim.command.claim.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public interface OtherCommandPermissionChecker extends CommandPermissionChecker {

    boolean checkOther(@NotNull CommandContext context);

}
