package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ClaimSubCommandManager {

    // TODO Improve this

    private final List<CommandNode<ClaimContextData>> commands;
    private final List<CommandNode<ClaimContextData>> specifics;

    public ClaimSubCommandManager() {
        commands = new LinkedList<>();
        specifics = new LinkedList<>();
    }

    public void register(@NotNull CommandNode<ClaimContextData> command, boolean isSpecific) {
        commands.add(command);
        if (isSpecific) {
            specifics.add(command);
        }
    }

    public void unregister(@NotNull CommandNode<ClaimContextData> command) {
        commands.remove(command);
        specifics.remove(command);
    }

    public List<CommandNode<ClaimContextData>> getCommands() {
        return commands;
    }

    public List<CommandNode<ClaimContextData>> getSpecifics() {
        return specifics;
    }

}
