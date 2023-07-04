package fr.flowsqy.stelyclaim.protocol.selection;

import com.sk89q.worldedit.regions.Region;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContextData;
import org.jetbrains.annotations.NotNull;

public interface SelectionModifier {

    void modify(@NotNull Region selection, @NotNull ActionContext<ClaimContextData> context);

}
