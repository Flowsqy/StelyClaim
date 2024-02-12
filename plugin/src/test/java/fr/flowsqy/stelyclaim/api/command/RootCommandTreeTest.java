package fr.flowsqy.stelyclaim.api.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RootCommandTreeTest {

    private final CommandNode rootNode;
    private final CommandNode statsNode;
    private final RootCommandTree rootCommandTree;

    public RootCommandTreeTest() {
        this.rootNode = new BasicNode("root", "root");
        final CommandNode helpNode = new BasicNode("help", "help");
        this.statsNode = new BasicNode("stats", "stats");
        final CommandTree helpTree = new SimpleCommandTree(helpNode, new CommandTree[0]);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, new CommandTree[0]); 
        this.rootCommandTree = new RootCommandTree(rootNode, new CommandTree[]{helpTree, statsTree});
    }

    @Test
    public void whenNoArgsThenRoot() {
        final CommandContext context = new CommandContext(new String[]{}, new FakePermissionCache());
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(rootNode, result.node());
        Assertions.assertEquals(0, context.getArgsLength());
    }

    @Test
    public void whenWrongArgsThenRoot() {
        final CommandContext context = new CommandContext(new String[]{"info"}, new FakePermissionCache());
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(rootNode, result.node());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("info", context.getArg(0));
    }

    @Test
    public void whenCorrectArgButNoPermThenRoot() {
        final CommandContext context = new CommandContext(new String[]{"stats"}, new FakePermissionCache());
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(rootNode, result.node());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("stats", context.getArg(0));
    }

    @Test
    public void whenCorrectArgAndPermThenChild() { 
        final CommandContext context = new CommandContext(new String[]{"stats"}, new FakePermissionCache("stats"));
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(statsNode, result.node());
        Assertions.assertEquals(0, context.getArgsLength());
    }

}
