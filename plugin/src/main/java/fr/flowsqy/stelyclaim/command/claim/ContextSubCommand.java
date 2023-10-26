package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.api.permission.PermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ContextSubCommand extends DispatchCommandTabExecutor implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final ClaimHandler<? extends ClaimOwner> claimHandler;
    private final ClaimSubCommandManager subCommandManager;
    private final PermissionChecker permChecker;
    private final HelpMessage helpMessage;

    public ContextSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers, @NotNull ClaimHandler<?> claimHandler,
                             @NotNull ClaimSubCommandManager subCommandManager, @NotNull PermissionChecker permChecker,
                             @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        this.claimHandler = claimHandler;
        this.subCommandManager = subCommandManager;
        this.permChecker = permChecker;
        this.helpMessage = helpMessage;
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
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
    public void execute(@NotNull CommandContext context) {
        context.setCustomData(new DefaultContext(claimHandler));
        super.execute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        context.setCustomData(new DefaultContext(claimHandler));
        return super.tabComplete(context);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext context) {
        return permChecker.checkBase(context);
    }

    @Override
    public @NotNull Iterable<CommandNode> getChildren() {
        return subCommandManager.getSpecifics();
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext context) {
        helpMessage.sendMessages(context, this, CommandNode::canExecute);
    }

}
