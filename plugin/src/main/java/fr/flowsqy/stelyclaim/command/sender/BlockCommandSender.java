package fr.flowsqy.stelyclaim.command.sender;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockCommandSender implements CommandSender {

    private final org.bukkit.command.BlockCommandSender block;

    public BlockCommandSender(org.bukkit.command.BlockCommandSender block) {
        this.block = block;
    }

    @Override
    public org.bukkit.command.CommandSender getBukkit() {
        return block;
    }

    @Override
    public boolean isPhysic() {
        return true;
    }

    @Override
    public PhysicCommandSender getPhysic() {
        return new PhysicImpl();
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public MovableCommandSender getMovable() {
        return null;
    }

    private class PhysicImpl implements PhysicCommandSender {

        @NotNull
        private final Block block;

        public PhysicImpl() {
            this.block = BlockCommandSender.this.block.getBlock();
        }

        @Override
        public @NotNull World getWorld() {
            return block.getWorld();
        }

        @Override
        public @NotNull Location getLocation() {
            return block.getLocation();
        }
    }

}
