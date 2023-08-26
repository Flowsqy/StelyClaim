package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.LazyHandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.WorldChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class InteractSubCommand implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final WorldChecker worldChecker;
    private final OtherPermissionChecker permChecker;
    private final HelpMessage helpMessage;

    public InteractSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                              @NotNull StelyClaimPlugin plugin,
                              @Nullable Collection<String> worlds, @NotNull OtherPermissionChecker permChecker,
                              @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        worldChecker = new WorldChecker(worlds, plugin.getMessages());
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (worldChecker.checkCancelledWorld(context.getActor())) {
            return;
        }
        if (!(context.getCustomData() instanceof final ClaimContext claimContext)) {
            throw new RuntimeException();
        }
        final LazyHandledOwner<?> lazyHandledOwner = claimContext.getOwnerContext().getLazyHandledOwner();
        if (context.getArgsLength() == 0) {
            final Actor sender = context.getActor();
            if (!sender.isPlayer()) {
                helpMessage.sendMessage(context, id);
                return;
            }
            lazyHandledOwner.retrieve(sender, sender.getPlayer());
        } else if (context.getArgsLength() == 1) {
            lazyHandledOwner.retrieve(context.getActor(), context.getArg(0));
        } else {
            helpMessage.sendMessage(context, id);
            return;
        }
        if (lazyHandledOwner.getOwner() == null) {
            // TODO Send a message to say that this owner can't be retrieved
            return;
        }
        claimContext.setWorld(() -> context.getActor().getPhysic().getWorld(), false);
        interactRegion(context);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @NotNull
    public OtherPermissionChecker getPermChecker() {
        return permChecker;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return context.getActor().isPhysic() && permChecker.checkBase(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        if (context.getArgsLength() != 1 || !permChecker.checkOther(context)) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith(arg))
                .collect(Collectors.toList());
    }

    protected abstract void interactRegion(@NotNull CommandContext context);

}
