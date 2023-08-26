package fr.flowsqy.stelyclaim.protocol;

import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;

public interface ProtocolInteractChecker {

    boolean canInteractNotOwned(@NotNull ActionContext context);

    default boolean canInteractGlobal(@NotNull ActionContext context) {
        return false;
    }

}
