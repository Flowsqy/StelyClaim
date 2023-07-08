package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContextSubCommand extends DispatchCommandTabExecutor<ClaimContext> implements CommandNode<ClaimContext> {

    private final String name;
    private final String[] triggers;
    private final ClaimHandler<? extends ClaimOwner> claimHandler;
    private final ClaimSubCommandManager subCommandManager;
    private final PermissionData data;
    private final HelpMessage helpMessage;

    public ContextSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull ClaimHandler<?> claimHandler, @NotNull ClaimSubCommandManager subCommandManager, @NotNull PermissionData data, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        this.claimHandler = claimHandler;
        this.subCommandManager = subCommandManager;
        this.data = data;
        this.helpMessage = helpMessage;
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
    public void execute(@NotNull CommandContext<ClaimContext> context) {
        context.getData().setHandler(claimHandler);
        super.execute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContext> context) {
        context.getCustomData().orElseThrow().getOwnerContext().setHandler(claimHandler);
        return super.tabComplete(context);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContext> context) {
        return context.hasPermission(data.getBasePerm(context.getData()));
    }

    @Override
    public @NotNull Iterable<CommandNode<ClaimContext>> getChildren() {
        return subCommandManager.getSpecifics();
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContext> context) {
        helpMessage.sendMessage(context);
    }

}
