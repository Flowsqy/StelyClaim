package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class HelpMessage {

    public record HelpData(@NotNull String command, @NotNull PermissionData data,
                           @NotNull Function<String, String> helpMessage) {
    }

    private final List<HelpData> helpDataList;

    public HelpMessage() {
        helpDataList = new LinkedList<>();
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context) {
        sendMessage(context, null);
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context, @Nullable String command) {
        sendMessage(context, command, false);
    }

    public void sendMessage(@NotNull CommandContext<ClaimContext> context, @Nullable String command, boolean reducedToSpecific) {
        final Optional<HelpData> specificData = helpDataList.stream().filter(data -> data.command().equalsIgnoreCase(command)).findAny();
        final Collection<HelpData> dataList = specificData.map(Collections::singletonList).orElse(helpDataList);
        final String handlerId = context.getData().getHandler().getId();
        for (HelpData data : dataList) {
            if (reducedToSpecific && !data.data().isContextSpecific()) {
                continue;
            }
            if (!context.hasPermission(data.data().getBasePerm(context.getData()))) {
                continue;
            }
            final String message = data.helpMessage().apply(handlerId);
            if (message != null) {
                context.getSender().getBukkit().sendMessage(message);
            }
        }
    }

    private int getIndex(@NotNull String command) {
        final Iterator<HelpData> helpDataIterator = helpDataList.iterator();
        for (int i = 0; helpDataIterator.hasNext(); i++) {
            final HelpData data = helpDataIterator.next();
            if (data.command().equalsIgnoreCase(command)) {
                return i;
            }
        }
        return -1;
    }

    public void registerCommand(@NotNull HelpData helpData) {
        if (getIndex(helpData.command) >= 0) {
            throw new IllegalArgumentException("Command already registered");
        }
        helpDataList.add(helpData);
    }

    public void unregisterCommand(@NotNull String command) {
        final int index = getIndex(command);
        if (getIndex(command) < 0) {
            throw new IllegalArgumentException("Command not registered");
        }
        helpDataList.remove(index);
    }

}
