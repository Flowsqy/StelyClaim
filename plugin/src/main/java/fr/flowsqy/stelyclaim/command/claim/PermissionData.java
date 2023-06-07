package fr.flowsqy.stelyclaim.command.claim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PermissionData {

    private final String basePerm;
    private final String parentPerm;
    private final boolean contextSpecific;

    public PermissionData(String basePerm, String parentPerm, boolean contextSpecific) {
        this.basePerm = basePerm;
        this.parentPerm = parentPerm;
        this.contextSpecific = contextSpecific;
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

}
