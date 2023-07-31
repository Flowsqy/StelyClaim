package fr.flowsqy.stelyclaim.protocol.selection;

import org.jetbrains.annotations.NotNull;

import com.sk89q.worldedit.regions.Region;

import fr.flowsqy.stelyclaim.api.action.ActionContext;

public interface SelectionModifier {

    void modify(@NotNull Region selection, @NotNull ActionContext context);

}
