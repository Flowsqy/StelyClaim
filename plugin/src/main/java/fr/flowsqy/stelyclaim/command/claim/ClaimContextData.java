package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClaimContextData {

    private ClaimHandler<?> handler;
    private String statistic;

    @NotNull
    public ClaimHandler<?> getHandler() {
        return handler;
    }

    @Nullable
    public String getStatistic() {
        return statistic;
    }

    public void setHandler(@NotNull ClaimHandler<?> handler) {
        this.handler = handler;
    }

    public void setStatistic(@Nullable String statistic) {
        this.statistic = statistic;
    }

}
