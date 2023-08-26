package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.CommandResolver;
import fr.flowsqy.stelyclaim.api.command.CommandResolver.Result;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import fr.flowsqy.stelyclaim.api.permission.PermissionChecker;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HelpSubCommand implements CommandNode, Identifiable {

    private final UUID id;
    private final String name;
    private final String[] triggers;
    private final PermissionChecker permChecker;
    private final DispatchCommandTabExecutor root;
    private final HelpMessage helpMessage;

    public HelpSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                          @NotNull PermissionChecker permChecker, @NotNull DispatchCommandTabExecutor root,
                          @NotNull HelpMessage helpMessage) {
        this.id = id;
        this.name = name;
        this.triggers = triggers;
        this.permChecker = permChecker;
        this.root = root;
        this.helpMessage = helpMessage;
    }

    @Override
    @NotNull
    public UUID getId() {
        return id;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        final CommandResolver resolver = new CommandResolver(root);
        // TODO Hardcoded -> bad
        final CommandContext fakeContext = CommandContext.buildFake(context, context.copyArgs(), "claim");
        final Result result = resolver.resolve(fakeContext, CommandNode::canExecute);
        if (result.isDispatcher()) {
            helpMessage.sendMessages(fakeContext, result.asDispatcher(), CommandNode::canExecute);
            return;
        }
        System.out.println(result.asNode().getName());
        helpMessage.sendMessage(fakeContext, result.asNode());
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

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        // final CommandResolver resolver = new CommandResolver(root);
        // TODO Hardcoded -> Bad
        final CommandContext fakeContext = CommandContext.buildFake(context, context.copyArgs(), "claim");
        final CommandResolver resolver = new CommandResolver(root);
        final Result result = resolver.resolve(fakeContext, CommandNode::canTabComplete);
        if (!result.isDispatcher() || context.getArgsLength() != 1) {
            return Collections.emptyList();
        }
        final String arg = context.getArg(0).toLowerCase(Locale.ENGLISH);
        final List<String> completions = new LinkedList<>();
        for (CommandNode command : result.asDispatcher().getChildren()) {
            if (!command.canTabComplete(context)) {
                continue;
            }
            final String completion = command.getName();
            if (!completion.startsWith(arg)) {
                continue;
            }
            completions.add(completion);
        }
        return completions;
    }

}
