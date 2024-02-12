package fr.flowsqy.stelyclaim.api.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SimpleCommandTreeTest {

    private final CommandNode rootNode;
    private final CommandNode resetNode;
    private final CommandNode statsNode;
    private final CommandNode helpNode;
    private final CommandTree rootCommandTree;

    public SimpleCommandTreeTest() {
        this.rootNode = new BasicNode("claim", "claim");
        this.helpNode = new BasicNode("help", "help");
        this.statsNode = new BasicNode("stats", "stats");
        this.resetNode = new BasicNode("reset", "reset");
        final CommandNode showNode = new BasicNode("show", "show");
        final CommandTree resetTree = new SimpleCommandTree(resetNode, new CommandTree[0]);
        final CommandTree showTree = new SimpleCommandTree(showNode, new CommandTree[0]);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, new CommandTree[]{showTree, resetTree});
        final CommandTree helpTree = new SimpleCommandTree(helpNode, new CommandTree[0]); 
        this.rootCommandTree = new SimpleCommandTree(rootNode, new CommandTree[]{helpTree, statsTree});
    }

    @Test
    public void whenNoArgsThenFail() {
        final CommandContext context = new CommandContext(new String[]{}, new FakePermissionCache());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rootCommandTree.resolve(context);
        });
    }

    @Test
    public void whenWrongArgThenNotSucceed() {
        final CommandContext context = new CommandContext(new String[]{"bedrock"}, new FakePermissionCache());
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(rootNode, result.node());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("bedrock", context.getArg(0));
        Assertions.assertEquals(false, result.success());
    }

    @Test
    public void whenCorrectArgsAndNoPermThenOwn() { 
        final CommandContext context = new CommandContext(new String[]{"claim", "stats"}, new FakePermissionCache("claim"));
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(rootNode, result.node());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("stats", context.getArg(0));
    }

    @Test
    public void whenCorrectArgsThenChild() { 
        final CommandContext context = new CommandContext(new String[]{"claim", "stats"}, new FakePermissionCache("claim", "stats"));
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(statsNode, result.node());
        Assertions.assertEquals(0, context.getArgsLength());
    }

    @Test
    public void whenCorrectArgsThenSubChild() { 
        final CommandContext context = new CommandContext(new String[]{"claim", "stats", "reset"}, new FakePermissionCache("claim", "stats", "reset"));
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertEquals(resetNode, result.node());
        Assertions.assertEquals(0, context.getArgsLength());
    }
}

