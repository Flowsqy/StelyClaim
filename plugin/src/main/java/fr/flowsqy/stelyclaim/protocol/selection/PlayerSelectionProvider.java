package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Region;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSelectionProvider implements SelectionProvider {

    @Nullable
    @Override
    public Region getSelection(@NotNull ActionContext<ClaimContext> context) {
        if (!context.getActor().isPlayer()) {
            throw new IllegalArgumentException("Actor should be a player");
        }
        final Player player = context.getActor().getPlayer();
        final LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        final com.sk89q.worldedit.world.World weWorld = new BukkitWorld(context.getCustomData().orElseThrow().getWorld().orElseThrow());

        if (!session.isSelectionDefined(weWorld)) {
            return null;
        }
        try {
            return session.getSelection(weWorld);
        } catch (IncompleteRegionException e) {
            throw new RuntimeException(e);
        }
    }

}
