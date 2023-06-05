package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClaimRootCommand extends DispatchCommandTabExecutor<ClaimContextData> {

    @Override
    public @NotNull List<CommandNode<ClaimContextData>> getChildren() {
        return null;
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContextData> context) {
        // TODO Send help message
    }

}
