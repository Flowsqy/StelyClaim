package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public class ClaimRootCommand extends DispatchCommandTabExecutor<ClaimContext> {

    private final ClaimSubCommandManager subCommandManager;
    private final HelpMessage helpMessage;

    public ClaimRootCommand(@NotNull ClaimSubCommandManager subCommandManager, @NotNull HelpMessage helpMessage) {
        this.subCommandManager = subCommandManager;
        this.helpMessage = helpMessage;
    }

    @Override
    public @NotNull Iterable<CommandNode<ClaimContext>> getChildren() {
        return subCommandManager.getCommands();
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContext> context) {
        helpMessage.sendMessage(context);
    }

}
