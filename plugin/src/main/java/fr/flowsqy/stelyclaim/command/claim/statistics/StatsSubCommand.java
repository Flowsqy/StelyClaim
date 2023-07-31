package fr.flowsqy.stelyclaim.command.claim.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.permission.CommandPermissionChecker;

public class StatsSubCommand extends DispatchCommandTabExecutor implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final CommandPermissionChecker permChecker;
    private final HelpMessage helpMessage;
    private final List<CommandNode> children;

    public StatsSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull CommandPermissionChecker permChecker, @NotNull HelpMessage helpMessage,
            @NotNull List<CommandNode> children) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
        this.children = new ArrayList<>(children);
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    /*
     * this.resetStatsSubCommand = new ResetStatsSubCommand(
     * plugin,
     * "reset",
     * "r",
     * permission + ".reset",
     * console,
     * plugin.getConfiguration().getStringList("worlds.stats.reset"),
     * statistic,
     * statisticManager
     * );
     * this.showStatsSubCommand = new ShowStatsSubCommand(
     * plugin,
     * "show",
     * "s",
     * permission + ".show",
     * console,
     * plugin.getConfiguration().getStringList("worlds.stats.show"),
     * statistic,
     * statisticManager
     * );
     */

    @Override
    public @NotNull Iterable<CommandNode> getChildren() {
        return children;
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext context) {
        helpMessage.sendMessages(context, this);
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return permChecker.checkBase(context);
    }

}
