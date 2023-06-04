package fr.flowsqy.stelyclaim.command.claim;

import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import org.jetbrains.annotations.NotNull;

public abstract class NamedSubCommand implements CommandNode<ClaimContextData> {

    private final String name;
    private final String[] triggers;

    public NamedSubCommand(@NotNull String name, @NotNull String[] aliases) {
        this.name = name;
        triggers = new String[aliases.length + 1];
        triggers[0] = name;
        System.arraycopy(aliases, 0, triggers, 1, aliases.length);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public @NotNull String[] getTriggers() {
        return triggers;
    }

    @Override
    public @NotNull String getTabCompletion() {
        return name;
    }

}
