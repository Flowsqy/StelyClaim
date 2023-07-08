package fr.flowsqy.stelyclaim.api.action;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ActionContext<T> {

    private final Actor actor;
    private final T customData;
    private ActionResult result;

    public ActionContext(@NotNull Actor actor, @Nullable T customData) {
        this.actor = actor;
        this.customData = customData;
    }

    @NotNull
    public Actor getActor() {
        return actor;
    }

    @NotNull
    public Optional<T> getCustomData() {
        return customData;
    }

    @Nullable
    public ActionResult getResult() {
        return result;
    }

    public void setResult(@Nullable ActionResult result) {
        this.result = result;
    }

}
