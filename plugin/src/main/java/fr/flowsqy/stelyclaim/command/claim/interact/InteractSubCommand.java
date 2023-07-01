package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.claim.*;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class InteractSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final WorldChecker worldChecker;
    private final PermissionData data;
    private final HelpMessage helpMessage;

    public InteractSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds, @NotNull PermissionData data, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        worldChecker = new WorldChecker(worlds, plugin.getMessages());
        this.data = data;
        this.helpMessage = helpMessage;
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        if (worldChecker.checkCancelledWorld(context.getSender())) {
            return;
        }
        final OwnerRetriever.Result<?> retrievedOwner;
        if (context.getArgsLength() == 0) {
            if (!context.getSender().isPlayer()) {
                helpMessage.sendMessage(context, name);
            }
            final Actor sender = context.getSender();
            retrievedOwner = OwnerRetriever.retrieve(sender, context.getData().getHandler(), sender.getPlayer());
        } else if (context.getArgsLength() == 1) {
            retrievedOwner = OwnerRetriever.retrieve(context.getSender(), context.getData().getHandler(), context.getArg(0));
        } else {
            helpMessage.sendMessage(context, name);
            return;
        }
        if (retrievedOwner.isEmpty()) {
            return;
        }
        final Actor sender = context.getSender();
        final boolean success = interactRegion(sender.getPhysic().getWorld(), sender, retrievedOwner.toHandledOwner());
        if (success) {
            context.getData().setStatistic(name);
        }
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
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.getSender().isPhysic() && context.hasPermission(data.getBasePerm(context.getData()));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() != 1 || !context.hasPermission(data.getModifierPerm(context.getData(), "other"))) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith(arg))
                .collect(Collectors.toList());
    }

    protected abstract <T extends ClaimOwner> boolean interactRegion(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> owner);

}
