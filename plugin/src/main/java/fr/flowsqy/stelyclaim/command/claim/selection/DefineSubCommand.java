package fr.flowsqy.stelyclaim.command.claim.selection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.selection.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class DefineSubCommand extends SelectionSubCommand {

    public DefineSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull OtherPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    protected void interactRegion(@NotNull CommandContext context) {
        context.getActor().getBukkit().spigot().sendMessage(new TextComponent("Define"));
        //new SelectionProtocol().process(context);
        final OverlappingRegion overlappingRegion = new OverlappingRegion();
        final SelectionProtocol protocol = new SelectionProtocol(
                new PlayerSelectionProvider(),
                new ExpandSelectionModifier(255, 0),
                new OverlappingValidator(overlappingRegion),
                new DefineHandler(null),
                getPermChecker()
        );
        protocol.process(context);
    }

}
