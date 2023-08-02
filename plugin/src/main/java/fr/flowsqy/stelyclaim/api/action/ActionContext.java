package fr.flowsqy.stelyclaim.api.action;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.api.actor.Actor;

public class ActionContext {

    private final Actor actor;
    private final Map<String, Boolean> permissions;
    private Object customData;
    private ActionResult result;

    public ActionContext(@NotNull Actor actor, @Nullable Object customData) {
        this.actor = actor;
        permissions = new HashMap<>();
        this.customData = customData;
    }

    @NotNull
    public Actor getActor() {
        return actor;
    }

    public boolean hasPermission(@NotNull String permission) {
        return permissions.computeIfAbsent(permission, getActor().getBukkit()::hasPermission);
    }

    @Nullable
    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(@Nullable Object customData) {
        this.customData = customData;
    }

    @Nullable
    public ActionResult getResult() {
        return result;
    }

    public void setResult(@Nullable ActionResult result) {
        this.result = result;
    }

}
