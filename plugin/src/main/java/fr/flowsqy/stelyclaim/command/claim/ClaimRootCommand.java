package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import org.jetbrains.annotations.NotNull;

public class ClaimRootCommand extends DispatchCommandTabExecutor {

    private final ClaimSubCommandManager subCommandManager;
    private final HelpMessage helpMessage;

    public ClaimRootCommand(@NotNull ClaimSubCommandManager subCommandManager, @NotNull HelpMessage helpMessage) {
        this.subCommandManager = subCommandManager;
        this.helpMessage = helpMessage;
    }

    @Override
    public @NotNull Iterable<CommandNode> getChildren() {
        return subCommandManager.getCommands();
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext context) {
        helpMessage.sendMessages(context, this);
    }

}
