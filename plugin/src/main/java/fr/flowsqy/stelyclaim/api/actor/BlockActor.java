package fr.flowsqy.stelyclaim.api.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BlockActor implements Actor {

    private final BlockCommandSender block;

    public BlockActor(BlockCommandSender block) {
        this.block = block;
    }

    @Override
    public CommandSender getBukkit() {
        return block;
    }

    @Override
    public boolean isPhysic() {
        return true;
    }

    @Override
    public PhysicActor getPhysic() {
        return new PhysicImpl();
    }

    private class PhysicImpl implements PhysicActor {

        @NotNull
        private final Block block;

        public PhysicImpl() {
            this.block = BlockActor.this.block.getBlock();
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
