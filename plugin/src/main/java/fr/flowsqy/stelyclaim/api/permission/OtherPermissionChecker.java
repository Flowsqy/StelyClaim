package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public interface OtherPermissionChecker extends PermissionChecker {

    boolean checkOther(@NotNull CommandContext context);

}
