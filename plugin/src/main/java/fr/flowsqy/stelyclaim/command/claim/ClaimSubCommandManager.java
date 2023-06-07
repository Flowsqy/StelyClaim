package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandNode;

import java.util.List;

public class ClaimSubCommandManager {

    private final List<CommandNode<ClaimContextData>> commands;

    public List<CommandNode<ClaimContextData>> getCommands() {
        return commands;
    }

    public List<CommandNode<ClaimContextData>> getSpecifics() {
    }

}
