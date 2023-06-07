package fr.flowsqy.stelyclaim.command.claim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClaimSubCommandData {

    private String basePerm;
    private String parentPerm;
    private boolean contextSpecific;

    public ClaimSubCommandData() {
    }

    public String getBasePerm(@Nullable ClaimContextData data) {
        return (parentPerm != null ? parentPerm : "")
                + (parentPerm != null && contextSpecific ? "." : "")
                + (contextSpecific ? (Objects.requireNonNull(data).getHandler().getId()) : "")
                + (contextSpecific && basePerm != null ? "." : "")
                + (basePerm != null ? basePerm : "");
    }

    public String getModifierPerm(@Nullable ClaimContextData data, @NotNull String modifier) {
        return getBasePerm(data) + "-" + modifier;
    }

    public boolean isContextSpecific() {
        return contextSpecific;
    }

    public void init(@Nullable String basePerm, @Nullable String parentPerm, boolean contextSpecific) {
        this.basePerm = basePerm;
        this.parentPerm = parentPerm;
        this.contextSpecific = contextSpecific;
    }

}
