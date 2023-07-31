package fr.flowsqy.stelyclaim.protocol.selection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldedit.regions.Region;

import fr.flowsqy.stelyclaim.api.action.ActionContext;

public interface SelectionProvider {

    @Nullable
    Region getSelection(@NotNull ActionContext context);

}
