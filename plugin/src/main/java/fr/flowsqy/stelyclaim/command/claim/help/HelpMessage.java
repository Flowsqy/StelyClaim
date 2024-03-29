package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.Identifiable;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.DispatchCommandTabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

public class HelpMessage {

    private final Map<UUID, HelpMessageProvider> providerRegistry;

    public HelpMessage() {
        providerRegistry = new HashMap<>();
    }

    public void register(@NotNull UUID commandId, @NotNull HelpMessageProvider provider) {
        providerRegistry.put(commandId, provider);
    }

    public void unregister(@NotNull UUID commandId) {
        providerRegistry.remove(commandId);
    }

    public void sendMessage(@NotNull CommandContext context, @NotNull UUID commandId) {
        // May check for context specific handler provider registry to allow custom help
        // messages
        // TODO Link this to claim handler
        final HelpMessageProvider messageProvider = providerRegistry.get(commandId);
        if (messageProvider == null) {
            return;
        }
        final String message = messageProvider.get(context);
        if (message == null) {
            return;
        }
        context.getActor().getBukkit().sendMessage(message);
    }

    public void sendMessage(@NotNull CommandContext context, @NotNull Identifiable identifiable) {
        sendMessage(context, identifiable.getId());
    }

    public void sendMessage(@NotNull CommandContext context, @NotNull CommandNode commandNode) {
        if (!(commandNode instanceof Identifiable identifiable)) {
            return;
        }
        sendMessage(context, identifiable);
    }

    public void sendMessages(@NotNull CommandContext context, @NotNull DispatchCommandTabExecutor dispatcher, @NotNull BiPredicate<CommandNode, CommandContext> nodePredicate) {
        for (CommandNode node : dispatcher.getChildren()) {
            if (!(node instanceof Identifiable identifiable)) {
                continue;
            }
            if (!nodePredicate.test(node, context)) {
                continue;
            }
            context.appendCommandName(node.getName());
            context.consumeArg();
            sendMessage(context, identifiable);
            context.removeLastCommandName();
            context.restoreArg();
        }
    }

}
