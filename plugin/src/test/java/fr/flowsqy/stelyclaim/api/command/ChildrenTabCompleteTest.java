package fr.flowsqy.stelyclaim.api.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.flowsqy.stelyclaim.api.command.CommandContext.ActionType;

public class ChildrenTabCompleteTest {

    private final List<CommandTree> children;

    public ChildrenTabCompleteTest() {
        children = new LinkedList<>();
        final GroupCommandTree subGroup = new GroupCommandTree(new LinkedList<>());
        final BasicNode helpNode = new BasicNode("help", "help");
        final BasicNode hereNode = new BasicNode("here", "here");
        final BasicNode infoNode = new BasicNode("info", "info");
        final CommandTree helpTree = new SimpleCommandTree(helpNode, GroupCommandTree.EMPTY);
        final CommandTree hereTree = new SimpleCommandTree(hereNode, GroupCommandTree.EMPTY);
        final CommandTree infoTree = new SimpleCommandTree(infoNode, GroupCommandTree.EMPTY);
        subGroup.getChildren().addAll(Arrays.asList(hereTree, infoTree));
        children.addAll(Arrays.asList(helpTree, subGroup));
    }

    @Test
    public void whenWrongArgThenNothing() {
        final ChildrenTabComplete ctb = new ChildrenTabComplete(children);
        final CommandContext context = new CommandContext(null, new String[]{"a"}, new FakePermissionCache(), ActionType.TAB_COMPLETE, null);
        final List<String> actual = ctb.tabComplete(context);
        Assertions.assertArrayEquals(new String[]{}, actual.toArray(new String[0]));
    }

    @Test
    public void whenCorrectThenOne() {
        final ChildrenTabComplete ctb = new ChildrenTabComplete(children);
        final CommandContext context = new CommandContext(null, new String[]{"help"}, new FakePermissionCache(), ActionType.TAB_COMPLETE, null);
        final List<String> actual = ctb.tabComplete(context);
        Assertions.assertArrayEquals(new String[]{"help"}, actual.toArray(new String[0]));
    }
    
    @Test
    public void whenCorrectInSubThenOne() {
        final ChildrenTabComplete ctb = new ChildrenTabComplete(children);
        final CommandContext context = new CommandContext(null, new String[]{"here"}, new FakePermissionCache(), ActionType.TAB_COMPLETE, null);
        final List<String> actual = ctb.tabComplete(context);
        Assertions.assertArrayEquals(new String[]{"here"}, actual.toArray(new String[0]));
    }

    @Test
    public void whenBeginingThenCompletions() {
        final ChildrenTabComplete ctb = new ChildrenTabComplete(children);
        final CommandContext context = new CommandContext(null, new String[]{"he"}, new FakePermissionCache(), ActionType.TAB_COMPLETE, null);
        final List<String> actual = ctb.tabComplete(context);
        Assertions.assertArrayEquals(new String[]{"help", "here"}, actual.toArray(new String[0]));
    }
 
    @Test
    public void whenNothingThenAll() {
        final ChildrenTabComplete ctb = new ChildrenTabComplete(children);
        final CommandContext context = new CommandContext(null, new String[]{""}, new FakePermissionCache(), ActionType.TAB_COMPLETE, null);
        final List<String> actual = ctb.tabComplete(context);
        Assertions.assertArrayEquals(new String[]{"help", "here", "info"}, actual.toArray(new String[0]));
    }

}

