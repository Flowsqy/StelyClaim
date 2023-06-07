package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HelpSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;
    private final ClaimSubCommandData data;
    private final ClaimSubCommandManager claimSubCommandManager;
    private final HelpMessage helpMessage;

    public HelpSubCommand(@NotNull String name, @NotNull String[] triggers, @NotNull ClaimSubCommandData data, @NotNull ClaimSubCommandManager claimSubCommandManager, @NotNull HelpMessage helpMessage) {
        this.name = name;
        this.triggers = triggers;
        this.data = data;
        this.claimSubCommandManager = claimSubCommandManager;
        this.helpMessage = helpMessage;
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        final String command;
        if (context.getArgsLength() > 1) {
            command = name;
        } else if (context.getArgsLength() == 1) {
            command = context.getArg(0);
        } else {
            command = null;
        }
        helpMessage.sendMessage(context, command);
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
        return context.hasPermission(data.getBasePerm(context.getData()));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() != 1) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        return claimSubCommandManager.getCommands().stream()
                .filter(cmd -> cmd.canTabComplete(context))
                .map(CommandNode::getTabCompletion)
                .filter(cmd -> cmd.startsWith(arg))
                .collect(Collectors.toList());
    }

}
