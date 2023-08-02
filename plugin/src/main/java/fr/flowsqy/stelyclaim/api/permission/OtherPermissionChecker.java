package fr.flowsqy.stelyclaim.api.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public interface OtherPermissionChecker extends PermissionChecker {

    boolean checkOther(@NotNull CommandContext context);

}
