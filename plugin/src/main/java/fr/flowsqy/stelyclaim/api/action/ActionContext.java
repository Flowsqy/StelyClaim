package fr.flowsqy.stelyclaim.api.action;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @NotNull
    public <T> T getCustomData(@NotNull Class<T> type) {
        return type.cast(customData);
    }

    public void setCustomData(@Nullable Object customData) {
        this.customData = customData;
    }

    @NotNull
    public Optional<ActionResult> getResult() {
        return Optional.ofNullable(result);
    }

    public void setResult(@Nullable ActionResult result) {
        this.result = result;
    }

}
