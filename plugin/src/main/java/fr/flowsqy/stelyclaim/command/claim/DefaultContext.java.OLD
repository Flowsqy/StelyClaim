package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import org.jetbrains.annotations.NotNull;

public class DefaultContext implements HandlerContext {

    private final ClaimHandler<?> handler;

    public DefaultContext(@NotNull ClaimHandler<?> handler) {
        this.handler = handler;
    }

    @NotNull
    public ClaimHandler<?> getHandler() {
        return handler;
    }

}
