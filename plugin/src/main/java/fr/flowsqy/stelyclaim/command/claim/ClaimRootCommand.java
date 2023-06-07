package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClaimRootCommand extends DispatchCommandTabExecutor<ClaimContextData> {

    private final ClaimSubCommandManager subCommandManager;
    private final HelpMessage helpMessage;

    public ClaimRootCommand(@NotNull ClaimSubCommandManager subCommandManager, @NotNull HelpMessage helpMessage) {
        this.subCommandManager = subCommandManager;
        this.helpMessage = helpMessage;
    }

    @Override
    public @NotNull List<CommandNode<ClaimContextData>> getChildren() {
        return subCommandManager.getCommands();
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContextData> context) {
        helpMessage.sendMessage(context);
    }

}
