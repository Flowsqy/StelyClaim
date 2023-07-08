package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public interface ProtocolInteractChecker {

    boolean canInteractNotOwned(@NotNull ActionContext<ClaimContext> context);

    default boolean canInteractGlobal(@NotNull ActionContext<ClaimContext> context) {
        return false;
    }

}
