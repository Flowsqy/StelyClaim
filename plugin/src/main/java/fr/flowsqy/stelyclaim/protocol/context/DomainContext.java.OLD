package fr.flowsqy.stelyclaim.protocol.context;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DomainContext extends InteractContext {

    private OfflinePlayer target;

    public DomainContext(@NotNull ClaimHandler<?> claimHandler) {
        super(claimHandler);
    }

    public Optional<OfflinePlayer> getTarget() {
        return Optional.ofNullable(target);
    }

    public void setTarget(OfflinePlayer target) {
        this.target = target;
    }

}
