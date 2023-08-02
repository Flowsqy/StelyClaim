package fr.flowsqy.stelyclaim.command.claim;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandNode;

public class ClaimSubCommandManager {

    // TODO Improve this

    private final List<CommandNode> commands;
    private final List<CommandNode> specifics;

    public ClaimSubCommandManager() {
        commands = new LinkedList<>();
        specifics = new LinkedList<>();
    }

    public void register(@NotNull CommandNode command, boolean isSpecific) {
        commands.add(command);
        if (isSpecific) {
            specifics.add(command);
        }
    }

    public void unregister(@NotNull CommandNode command) {
        commands.remove(command);
        specifics.remove(command);
    }

    @NotNull
    public Iterable<CommandNode> getCommands() {
        return commands;
    }

    @NotNull
    public Iterable<CommandNode> getSpecifics() {
        return specifics;
    }

}
