package fr.flowsqy.stelyclaim.command.claim.selection;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class RedefineSubCommand extends SelectionSubCommand {

    public RedefineSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                              @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds,
                              @NotNull OtherPermissionChecker permChecker, @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, worlds, permChecker, helpMessage);
    }

    @Override
    protected void interactRegion(@NotNull CommandContext context) {
        context.getActor().getBukkit().spigot().sendMessage(new TextComponent("Redefine"));
    }

}
