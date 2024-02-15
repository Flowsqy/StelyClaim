package fr.flowsqy.stelyclaim.api.command;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.flowsqy.stelyclaim.api.command.CommandContext.ActionType;

public class GroupCommandTreeTest {

    private final GroupCommandTree groupCommandTree;
    private final CommandNode helpNode;

    public GroupCommandTreeTest() {
        helpNode = new BasicNode("help", "help");
        final CommandNode statsNode = new BasicNode("stats", "stats");
        final CommandTree helpTree = new SimpleCommandTree(helpNode, GroupCommandTree.EMPTY);
        final CommandTree statsTree = new SimpleCommandTree(statsNode, GroupCommandTree.EMPTY); 
        groupCommandTree = new GroupCommandTree(new LinkedList<>());
        groupCommandTree.getChildren().addAll(Arrays.asList(helpTree, statsTree));
    }

    @Test
    public void whenWrongArgThenNothing() {
        final CommandContext context = new CommandContext(null, new String[]{"info"}, new FakePermissionCache(), ActionType.UNKNOWN, null);
        final ResolveResult result = groupCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isEmpty());
        Assertions.assertEquals(1, context.getArgsLength());
        Assertions.assertEquals("info", context.getArg(0));
    }

    @Test
    public void whenCorrectArgThenChild() {
        final CommandContext context = new CommandContext(null, new String[]{"help"}, new FakePermissionCache("help"), ActionType.UNKNOWN, null);
        final ResolveResult result = groupCommandTree.resolve(context);
        Assertions.assertTrue(result.node().isPresent());
        Assertions.assertEquals(helpNode, result.node().get());
        Assertions.assertEquals(0, context.getArgsLength());
    }

}

