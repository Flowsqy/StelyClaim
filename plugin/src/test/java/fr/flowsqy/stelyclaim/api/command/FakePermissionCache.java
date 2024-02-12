package fr.flowsqy.stelyclaim.api.command;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

class FakePermissionCache extends PermissionCache {

    private final Set<String> permissions;

    public FakePermissionCache(@NotNull String... permissions) {
        super(null);
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return permissions.contains(permission);
    }

}
