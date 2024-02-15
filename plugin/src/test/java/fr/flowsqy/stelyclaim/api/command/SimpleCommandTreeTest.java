package fr.flowsqy.stelyclaim.api.command;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.flowsqy.stelyclaim.api.command.CommandContext.ActionType;

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
        final CommandTree resetTree = new SimpleCommandTree(resetNode, GroupCommandTree.EMPTY);
        final CommandTree showTree = new SimpleCommandTree(showNode, GroupCommandTree.EMPTY);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, new GroupCommandTree(new LinkedList<>(Arrays.asList(new CommandTree[]{showTree, resetTree}))));
        final CommandTree helpTree = new SimpleCommandTree(helpNode, GroupCommandTree.EMPTY);
        this.rootCommandTree = new SimpleCommandTree(rootNode, new GroupCommandTree(new LinkedList<>(Arrays.asList(helpTree, statsTree))));
    }

    @Test
    public void whenNoArgsThenFail() {
        final CommandContext context = new CommandContext(null, new String[]{}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rootCommandTree.resolve(context);
        });
    }

    @Test
    public void whenWrongArgThenEmpty() {
        final CommandContext context = new CommandContext(null, new String[]{"bedrock"}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isEmpty());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("bedrock", context.getArg(0));
    }

    @Test
    public void whenCorrectArgsAndNoPermThenOwn() { 
        final CommandContext context = new CommandContext(null, new String[]{"claim", "stats"}, new FakePermissionCache("claim"), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(rootNode, result.node().get());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("stats", context.getArg(0));
    }

    @Test
    public void whenCorrectArgsThenChild() { 
        final CommandContext context = new CommandContext(null, new String[]{"claim", "stats"}, new FakePermissionCache("claim", "stats"), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(statsNode, result.node().get());
        Assertions.assertEquals(0, context.getArgsLength());
    }

    @Test
    public void whenCorrectArgsThenSubChild() { 
        final CommandContext context = new CommandContext(null, new String[]{"claim", "stats", "reset", "tree"}, new FakePermissionCache("claim", "stats", "reset"), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(resetNode, result.node().get());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("tree", context.getArg(0));
    }
}

