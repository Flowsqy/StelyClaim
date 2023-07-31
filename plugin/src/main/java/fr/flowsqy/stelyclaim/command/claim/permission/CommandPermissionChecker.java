package fr.flowsqy.stelyclaim.command.claim.permission;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public interface CommandPermissionChecker {

    boolean checkBase(@NotNull CommandContext context);

    /*
     * private final String basePerm;
     * private final String parentPerm;
     * private final boolean contextSpecific;
     * 
     * public CommandPermissionChecker(String basePerm, String parentPerm, boolean
     * contextSpecific) {
     * this.basePerm = basePerm;
     * this.parentPerm = parentPerm;
     * this.contextSpecific = contextSpecific;
     * }
     * 
     * public String getBasePerm(@Nullable ClaimContext data) {
     * return (parentPerm != null ? parentPerm : "")
     * + (parentPerm != null && contextSpecific ? "." : "")
     * + (contextSpecific ? (Objects.requireNonNull(data).getHandler().getId()) :
     * "")
     * + (contextSpecific && basePerm != null ? "." : "")
     * + (basePerm != null ? basePerm : "");
     * }
     * 
     * public String getModifierPerm(@Nullable ClaimContext data, @NotNull String
     * modifier) {
     * return getBasePerm(data) + "-" + modifier;
     * }
     * 
     * public boolean isContextSpecific() {
     * return contextSpecific;
     * }
     */

}
