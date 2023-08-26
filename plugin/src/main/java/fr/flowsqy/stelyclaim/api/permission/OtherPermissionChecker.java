package fr.flowsqy.stelyclaim.api.permission;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public interface OtherPermissionChecker extends PermissionChecker {

    boolean checkOther(@NotNull ActionContext context);

}
