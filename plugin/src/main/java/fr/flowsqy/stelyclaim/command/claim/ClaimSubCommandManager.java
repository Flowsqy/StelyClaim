package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ClaimSubCommandManager {

    // TODO Improve this

    private final List<CommandNode<ClaimContext>> commands;
    private final List<CommandNode<ClaimContext>> specifics;

    public ClaimSubCommandManager() {
        commands = new LinkedList<>();
        specifics = new LinkedList<>();
    }

    public void register(@NotNull CommandNode<ClaimContext> command, boolean isSpecific) {
        commands.add(command);
        if (isSpecific) {
            specifics.add(command);
        }
    }

    public void unregister(@NotNull CommandNode<ClaimContext> command) {
        commands.remove(command);
        specifics.remove(command);
    }

    public Iterable<CommandNode<ClaimContext>> getCommands() {
        return commands;
    }

    public Iterable<CommandNode<ClaimContext>> getSpecifics() {
        return specifics;
    }

}
