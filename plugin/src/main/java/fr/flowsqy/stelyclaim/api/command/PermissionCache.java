package fr.flowsqy.stelyclaim.api.command;

import fr.flowsqy.stelyclaim.api.actor.Actor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PermissionCache {

    private final Actor actor;
    private final Map<String, Boolean> cache;

    public PermissionCache(@NotNull Actor actor) {
        this.actor = actor;
        cache = new HashMap<>();
    }

    public boolean hasPermission(@NotNull String permission) {
        final Boolean cachedValue = cache.get(permission);
        if (cachedValue != null) {
            return cachedValue;
        }
        final boolean value = actor.getBukkit().hasPermission(permission);
        cache.put(permission, value);
        return value;
    }

}

