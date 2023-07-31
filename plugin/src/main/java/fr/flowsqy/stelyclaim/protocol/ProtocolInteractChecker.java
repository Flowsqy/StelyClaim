package fr.flowsqy.stelyclaim.protocol;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.action.ActionContext;

public interface ProtocolInteractChecker {

    boolean canInteractNotOwned(@NotNull ActionContext context);

    default boolean canInteractGlobal(@NotNull ActionContext context) {
        return false;
    }

}
