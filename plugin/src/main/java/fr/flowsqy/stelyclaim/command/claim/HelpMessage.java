package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class HelpMessage {

    public record HelpData(@NotNull String command, boolean contextual,
                           @NotNull Function<CommandContext<ClaimContext>, String> messageProvider) {
    }

    private HelpData[] dataArray;

    public HelpMessage() {
        dataArray = new HelpData[0];
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context, @Nullable String command, boolean reducedToContextual) {
        final int index = command == null ? -1 : getIndex(command);
        final HelpData[] toProcessData = index >= 0 ? new HelpData[]{dataArray[index]} : dataArray;
        for (HelpData data : toProcessData) {
            if (reducedToContextual && !data.contextual()) {
                continue;
            }
            final String message = data.messageProvider().apply(context);
            if (message != null) {
                context.getActor().getBukkit().sendMessage(message);
            }
        }
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context) {
        sendMessage(context, null);
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context, @Nullable String command) {
        sendMessage(context, command, false);
    }

    private int getIndex(@NotNull String command) {
        for (int index = 0; index < dataArray.length; index++) {
            if (dataArray[index].command().equalsIgnoreCase(command)) {
                return index;
            }
        }
        return -1;
    }

    // Slow operation to save memory at runtime
    // Should only be used at configuration / initialization phase
    public void registerCommand(@NotNull HelpData helpData) {
        if (getIndex(helpData.command) >= 0) {
            throw new IllegalArgumentException("Command already registered");
        }
        final HelpData[] newArray = new HelpData[dataArray.length + 1];
        System.arraycopy(dataArray, 0, newArray, 0, dataArray.length);
        newArray[dataArray.length] = helpData;
        dataArray = newArray;
    }

    // Slow operation to save memory at runtime
    // Should only be used at configuration / initialization phase
    public void unregisterCommand(@NotNull String command) {
        final int index = getIndex(command);
        if (getIndex(command) < 0) {
            throw new IllegalArgumentException("Command not registered");
        }
        final HelpData[] newArray = new HelpData[dataArray.length - 1];
        System.arraycopy(dataArray, 0, newArray, 0, index);
        System.arraycopy(dataArray, index + 1, newArray, index, dataArray.length - index - 1);
        dataArray = newArray;
    }

}
