package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.Region;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectionProvider {

    @Nullable
    Region getSelection(@NotNull ActionContext context);

}
