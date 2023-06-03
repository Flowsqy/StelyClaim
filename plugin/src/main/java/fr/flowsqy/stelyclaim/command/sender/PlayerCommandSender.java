package fr.flowsqy.stelyclaim.command.sender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommandSender extends EntityCommandSender<Player> {

    public PlayerCommandSender(@NotNull Player entity) {
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
