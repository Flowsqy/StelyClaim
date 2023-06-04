package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContextSubCommand extends DispatchCommandTabExecutor<ClaimContextData> implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final ClaimHandler<?> claimHandler;

    public ContextSubCommand(String name, String[] triggers, ClaimHandler<?> claimHandler) {
        this.name = name;
        this.triggers = triggers;
        this.claimHandler = claimHandler;
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
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        context.getData().setHandler(claimHandler);
        super.execute(context);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        context.getData().setHandler(claimHandler);
        return super.tabComplete(context);
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.hasPermission(getBasePerm());
    }

    @Override
    public @NotNull List<CommandNode<ClaimContextData>> getChildren() {
        return null; // TODO Get specific sub commands
    }

    @Override
    public void fallBackExecute(@NotNull CommandContext<ClaimContextData> context) {
        new HelpMessage().sendMessage(context); // TODO Specify context
    }

}
