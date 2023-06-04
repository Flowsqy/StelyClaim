package fr.flowsqy.stelyclaim.api.actor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerActor extends EntityActor<Player> {

    public PlayerActor(@NotNull Player entity) {
        super(entity);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public Player getPlayer() {
        return entity;
    }
}
