package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.LazyHandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.PermissionData;
import fr.flowsqy.stelyclaim.command.claim.WorldChecker;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class DomainSubCommand implements CommandNode<ClaimContext> {

    private final String name;
    private final String[] triggers;
    private final WorldChecker worldChecker;
    private final PermissionData data;
    private final HelpMessage helpMessage;

    public DomainSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull PermissionData data, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        worldChecker = new WorldChecker(worlds, plugin.getMessages());
        this.data = data;
        this.helpMessage = helpMessage;
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContext> context) {
        if (worldChecker.checkCancelledWorld(context.getActor())) {
            return;
        }
        final ClaimContext claimContext = context.getCustomData().orElseThrow();
        final LazyHandledOwner<?> lazyOwner = claimContext.getOwnerContext().getLazyHandledOwner();
        final String target;
        if (context.getArgsLength() == 1) {
            final Actor actor = context.getActor();
            if (!actor.isPlayer()) {
                helpMessage.sendMessage(context, name);
                return;
            }
            lazyOwner.retrieve(actor, actor.getPlayer());
            target = context.getArg(0);
        } else if (context.getArgsLength() == 2) {
            lazyOwner.retrieve(context.getActor(), context.getArg(0));
            target = context.getArg(1);
        } else {
            helpMessage.sendMessage(context, name);
            return;
        }

        if (lazyOwner.getOwner() == null) {
            // Could not retrieve the owner
            // TODO Maybe send a message
            return;
        }

        final OfflinePlayer targetPlayer = OfflinePlayerRetriever.getOfflinePlayer(target);
        final Actor sender = context.getActor();
        claimContext.setWorld(() -> sender.getPhysic().getWorld(), false);

        interact(context, targetPlayer);
        /* Stats stuff.
        if (success) {
            context.getData().setStatistic(name);
        }*/
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContext> context) {
        return context.getActor().isPhysic() && context.hasPermission(data.getBasePerm(context.getData()));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContext> context) {
        final int size = context.getArgsLength();
        if (size != 1 && !(size == 2 || context.hasPermission(data.getModifierPerm(context.getData(), "other")))) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(context.getArgsLength() - 1).toLowerCase(Locale.ENGLISH);
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith(arg))
                .collect(Collectors.toList());
    }

    protected abstract void interact(@NotNull CommandContext<ClaimContext> context, @NotNull OfflinePlayer target);

}
