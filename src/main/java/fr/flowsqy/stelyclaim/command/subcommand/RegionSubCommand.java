package fr.flowsqy.stelyclaim.command.subcommand;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.*;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public abstract class RegionSubCommand extends SubCommand {

    protected final RegionContainer regionContainer;

    public RegionSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        regionContainer = plugin.getRegionContainer();
    }

    protected final RegionManager getRegionManager(String worldName) {
        return regionContainer.get(new WorldName(worldName));
    }

    protected final RegionManager getRegionManager(com.sk89q.worldedit.world.World world) {
        return regionContainer.get(world);
    }

    protected final RegionManager getRegionManager(World world) {
        return getRegionManager(world.getName());
    }

    private static final class WorldName implements com.sk89q.worldedit.world.World {

        final String name;

        public WorldName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public Path getStoragePath() {
            return null;
        }

        @Override
        public int getMinY() {
            return 0;
        }

        @Override
        public int getMaxY() {
            return 0;
        }

        @Override
        public Mask createLiquidMask() {
            return null;
        }

        @Override
        public boolean useItem(BlockVector3 blockVector3, BaseItem baseItem, Direction direction) {
            return false;
        }

        @Override
        public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 blockVector3, B b, SideEffectSet sideEffectSet) {
            return false;
        }

        @Override
        public Set<SideEffect> applySideEffects(BlockVector3 blockVector3, BlockState blockState, SideEffectSet sideEffectSet) {
            return null;
        }

        @Override
        public int getBlockLightLevel(BlockVector3 blockVector3) {
            return 0;
        }

        @Override
        public boolean clearContainerBlockContents(BlockVector3 blockVector3) {
            return false;
        }

        @Override
        public void dropItem(Vector3 vector3, BaseItemStack baseItemStack, int i) {

        }

        @Override
        public void dropItem(Vector3 vector3, BaseItemStack baseItemStack) {

        }

        @Override
        public void simulateBlockMine(BlockVector3 blockVector3) {

        }

        @Override
        public boolean generateTree(TreeGenerator.TreeType treeType, EditSession editSession, BlockVector3 blockVector3) {
            return false;
        }

        @Override
        public void checkLoadedChunk(BlockVector3 blockVector3) {

        }

        @Override
        public void fixAfterFastMode(Iterable<BlockVector2> iterable) {

        }

        @Override
        public void fixLighting(Iterable<BlockVector2> iterable) {

        }

        @Override
        public boolean playEffect(Vector3 vector3, int i, int i1) {
            return false;
        }

        @Override
        public boolean queueBlockBreakEffect(Platform platform, BlockVector3 blockVector3, BlockType blockType, double v) {
            return false;
        }

        @Override
        public WeatherType getWeather() {
            return null;
        }

        @Override
        public void setWeather(WeatherType weatherType) {

        }

        @Override
        public long getRemainingWeatherDuration() {
            return 0;
        }

        @Override
        public void setWeather(WeatherType weatherType, long l) {

        }

        @Override
        public BlockVector3 getSpawnPosition() {
            return null;
        }

        @Override
        public BlockVector3 getMinimumPoint() {
            return null;
        }

        @Override
        public BlockVector3 getMaximumPoint() {
            return null;
        }

        @Override
        public List<? extends Entity> getEntities(Region region) {
            return null;
        }

        @Override
        public List<? extends Entity> getEntities() {
            return null;
        }

        @Nullable
        @Override
        public Entity createEntity(Location location, BaseEntity baseEntity) {
            return null;
        }

        @Override
        public BlockState getBlock(BlockVector3 blockVector3) {
            return null;
        }

        @Override
        public BaseBlock getFullBlock(BlockVector3 blockVector3) {
            return null;
        }

        @Override
        public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 blockVector3, T t) {
            return false;
        }

        @Nullable
        @Override
        public Operation commit() {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }
    }

}
