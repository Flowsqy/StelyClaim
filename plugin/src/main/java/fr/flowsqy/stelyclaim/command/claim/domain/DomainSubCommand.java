package fr.flowsqy.stelyclaim.command.claim.domain;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.OwnerRetriever;
import fr.flowsqy.stelyclaim.command.claim.WorldChecker;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.util.OfflinePlayerRetriever;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class DomainSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final WorldChecker worldChecker;

    public DomainSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull StelyClaimPlugin plugin, @Nullable Collection<String> worlds) {
        this.name = name;
        this.triggers = triggers;
        worldChecker = new WorldChecker(worlds, plugin.getMessages());
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        if (worldChecker.checkCancelledWorld(context.getSender())) {
            return;
        }
        final OwnerRetriever.Result<?> owner;
        final String target;
        if (context.getArgsLength() == 1) {
            if (!context.getSender().isPlayer()) {
                new HelpMessage().sendMessage(context); // TODO Specify name
                return;
            }
            owner = OwnerRetriever.retrieve(context.getSender(), context.getData().getHandler(), context.getSender().getPlayer());
            target = context.getArg(0);
        } else if (context.getArgsLength() == 2) {
            owner = OwnerRetriever.retrieve(context.getSender(), context.getData().getHandler(), context.getArg(0));
            target = context.getArg(1);
        } else {
            new HelpMessage().sendMessage(context); // TODO Specify name
            return;
        }

        if (owner.isEmpty()) {
            return;
        }

        final OfflinePlayer targetPlayer = OfflinePlayerRetriever.getOfflinePlayer(target);
        final Actor sender = context.getSender();
        final boolean success = interact(sender, sender.getPhysic().getWorld(), owner.toHandledOwner(), targetPlayer);
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
        return context.getSender().isPhysic() && context.hasPermission(getBasePerm());
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        final int size = context.getArgsLength();
        if (size != 1 && !(size == 2 || context.hasPermission(getOtherPerm()))) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(context.getArgsLength() - 1).toLowerCase(Locale.ENGLISH);
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith(arg))
                .collect(Collectors.toList());
    }

    protected abstract <T extends ClaimOwner> boolean interact(@NotNull Actor actor, @NotNull World world, @NotNull HandledOwner<T> owner, @NotNull OfflinePlayer target);

}
