package fr.flowsqy.stelyclaim.api.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import fr.flowsqy.stelyclaim.api.command.CommandNode;
import fr.flowsqy.stelyclaim.api.command.CommandTree;
import fr.flowsqy.stelyclaim.api.command.SimpleCommandTree;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import java.util.List;

public class SimpleCommandTreeTest {

    private static class BasicNode implements CommandNode {

        private final String name;

        public BasicNode(String name) {
            this.name = name;
        }

        public ResolveResult resolve(CommandContext context) {
            final String arg = context.getArg(0);
            if (arg.equalsIgnoreCase(name)) {
                return new ResolveResult(this, true, true);
            }
            return new ResolveResult(this, false, false);
        }

        public void execute(ActionContext c) {
        }

        public List<String> tabComplete(CommandContext c) {
            return null;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    @Test
    public void doWhatIWant() {
        final CommandNode helpNode = new BasicNode("help");
        final CommandNode resetNode = new BasicNode("reset");
        final CommandNode showNode = new BasicNode("show");
        final CommandNode statsNode = new BasicNode("stats");
        final CommandNode claimNode = new BasicNode("claim");
        final CommandTree helpTree = new SimpleCommandTree(helpNode, new CommandTree[0]);
        final CommandTree resetTree = new SimpleCommandTree(resetNode, new CommandTree[0]);
        final CommandTree showTree = new SimpleCommandTree(showNode, new CommandTree[0]);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, new CommandTree[]{showTree, resetTree});
        final CommandTree claimTree = new SimpleCommandTree(claimNode, new CommandTree[]{helpTree, statsTree}); 
        CommandContext c = new CommandContext(new String[]{"claim"}, null);
        ResolveResult r = claimTree.resolve(c);
        Assertions.assertEquals(claimNode, r.node());
        c = new CommandContext(new String[]{"claim", "stats", "hello"}, null);
        r = claimTree.resolve(c);
        Assertions.assertEquals(statsNode, r.node());
    }
}

