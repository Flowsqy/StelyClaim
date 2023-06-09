package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class HandlerRegistry {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+$");

    private final Map<String, ClaimHandler<?>> handlers;

    public HandlerRegistry() {
        handlers = new HashMap<>();
    }

    public void registerHandler(@NotNull ClaimHandler<?> handler) {
        Objects.requireNonNull(handler);
        final String id = handler.getId();
        Objects.requireNonNull(id);
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("Invalid id, must match pattern " + ID_PATTERN.pattern());
        }
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("A ClaimHandler is already register for the id '" + id + "'");
        }
        handlers.put(id, handler);
    }

    @SuppressWarnings("unused") //API
    public boolean unregisterHandler(@NotNull ClaimHandler<?> handler) {
        Objects.requireNonNull(handler);
        return handlers.remove(handler.getId()) != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends ClaimOwner, S extends ClaimHandler<T>> S getHandler(@NotNull String id) {
        return id == null ? null : (S) handlers.get(id);
    }

}
