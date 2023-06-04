package fr.flowsqy.stelyclaim.command.claim.interact;

import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.claim.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.OwnerRetriever;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class InteractSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;

    public InteractSubCommand(@NotNull String name, @NotNull String[] triggers) {
        this.name = name;
        this.triggers = triggers;
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        final OwnerRetriever.Result<?> retrievedOwner;
        if (context.getArgsLength() == 0) {
            if (!context.getSender().isPlayer()) {
                new HelpMessage().sendMessage(context); // TODO Specify name
            }
            final Actor sender = context.getSender();
            retrievedOwner = OwnerRetriever.retrieve(sender, context.getData().getHandler(), sender.getPlayer());
        } else if (context.getArgsLength() == 1) {
            retrievedOwner = OwnerRetriever.retrieve(context.getSender(), context.getData().getHandler(), context.getArg(0));
        } else {
            new HelpMessage().sendMessage(context); // TODO Specify name
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
        return context.getSender().isPhysic() && context.hasPermission(getBasePerm());
    }

    @Override
    public boolean canTabComplete(@NotNull CommandContext<ClaimContextData> context) {
        return canExecute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() != 1 || !context.hasPermission(getOtherPermission)) {
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
