package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.command.struct.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HelpSubCommand implements CommandNode<ClaimContextData> {

    private final static String NAME = "help";
    private final static String[] TRIGGERS = new String[]{NAME, "h"};
    private final DispatchCommandTabExecutor<ClaimContextData> rootCommandExecutor;
    private final HelpMessage helpMessage;

    public HelpSubCommand() {
    }

    @Override
    public void execute(@NotNull CommandContext<ClaimContextData> context) {
        final String command;
        if (context.getArgsLength() > 1) {
            command = "help";
        } else if (context.getArgsLength() == 1) {
            command = context.getArg(0);
        } else {
            command = null;
        }
        helpMessage.sendMessage(context); // TODO Specify command
    }

    @Override
    public @NotNull String[] getTriggers() {
        return TRIGGERS;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return NAME;
    }

    @Override
    public boolean canExecute(@NotNull CommandContext<ClaimContextData> context) {
        return context.hasPermission(getBasePerm());
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
        if (context.getArgsLength() != 1) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        return rootCommandExecutor.getChildren().stream()
                .map(CommandNode::getTabCompletion)
                .filter(cmd -> cmd.startsWith(arg))
                .collect(Collectors.toList());
    }

}
