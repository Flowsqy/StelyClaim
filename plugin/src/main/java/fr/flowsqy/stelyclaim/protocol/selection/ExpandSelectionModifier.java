package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class ExpandSelectionModifier implements SelectionModifier {

    private final int maxY;
    private final int minY;

    public ExpandSelectionModifier(int maxY, int minY) {
        this.maxY = maxY;
        this.minY = minY;
    }

    @Override
    public void modify(@NotNull Region selection, @NotNull ActionContext context) {
        try {
            selection.expand(
                    BlockVector3.ZERO.withY(maxY - selection.getMaximumPoint().getY()),
                    BlockVector3.ZERO.withY(minY - selection.getMinimumPoint().getY())
            );
        } catch (RegionOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
