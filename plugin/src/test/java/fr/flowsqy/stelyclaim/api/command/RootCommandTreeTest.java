package fr.flowsqy.stelyclaim.api.command;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.flowsqy.stelyclaim.api.command.CommandContext.ActionType;

public class RootCommandTreeTest {

    private final CommandNode rootNode;
    private final CommandNode statsNode;
    private final RootCommandTree rootCommandTree;

    public RootCommandTreeTest() {
        this.rootNode = new BasicNode("root", "root");
        final CommandNode helpNode = new BasicNode("help", "help");
        this.statsNode = new BasicNode("stats", "stats");
        final CommandTree helpTree = new SimpleCommandTree(helpNode, GroupCommandTree.EMPTY);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, GroupCommandTree.EMPTY);
        this.rootCommandTree = new RootCommandTree(rootNode, new GroupCommandTree(new LinkedList<>(Arrays.asList(helpTree, statsTree))));
    }

    @Test
    public void whenNoArgsThenRoot() {
        final CommandContext context = new CommandContext(null, new String[]{}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(rootNode, result.node().get());
        Assertions.assertEquals(0, context.getArgsLength());
    }

    @Test
    public void whenWrongArgsThenRoot() {
        final CommandContext context = new CommandContext(null, new String[]{"info"}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(rootNode, result.node().get());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("info", context.getArg(0));
    }

    @Test
    public void whenCorrectArgButNoPermThenRoot() {
        final CommandContext context = new CommandContext(null, new String[]{"stats"}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(rootNode, result.node().get());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("stats", context.getArg(0));
    }

    @Test
    public void whenCorrectArgAndPermThenChild() { 
        final CommandContext context = new CommandContext(null, new String[]{"stats"}, new FakePermissionCache("stats"), ActionType.UNKNOWN, null);
        final ResolveResult result = rootCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(statsNode, result.node().get());
        Assertions.assertEquals(0, context.getArgsLength());
    }

}
