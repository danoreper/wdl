package org.broadinstitute.compositetask;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.broadinstitute.parser.SyntaxError;

import org.broadinstitute.compositetask.CompositeTask;

public class ApiTest
{
    private CompositeTask task;

    @BeforeClass
    private void createCompositeTaskObject() {
        try {
            File source = new File("test-files/api/0.wdl");
            String relative = new File(".").toURI().relativize(source.toURI()).getPath();
            this.task = new CompositeTask(source, relative);
        } catch(IOException error) {
            Assert.fail("IOException reading file: " + error);
        } catch(SyntaxError error) {
            Assert.fail("Not expecting syntax error: " + error);
        }
    }

    @Test
    public void testGetNodesMethodReturnsTheRightNumberOfNodes() {
        Set<CompositeTaskNode> nodes = this.task.getNodes();
        Assert.assertEquals(nodes.size(), 2, "Expecting 2 nodes, found " + nodes.size());
    }

    @Test
    public void testGetNodesMethodReturnsASingleTopLevelStepNode() {
        Set<CompositeTaskNode> nodes = this.task.getNodes();
        boolean found = false;
        for ( CompositeTaskNode node : nodes ) {
            if ( node instanceof CompositeTaskStep ) {
                found = true;
                CompositeTaskStep step = (CompositeTaskStep) node;
                Assert.assertEquals(step.getName(), "atask", "Expecting one step with name 'atask'");
            }
        }
        Assert.assertTrue(found, "Expected one top-level step in the composite task, didn't find any");
    }

    @Test
    public void testGetNodesMethodReturnsASingleTopLevelForLoopNode() {
        Set<CompositeTaskNode> nodes = this.task.getNodes();
        boolean found = false;
        for ( CompositeTaskNode node : nodes ) {
            if ( node instanceof CompositeTaskForLoop ) {
                found = true;
                CompositeTaskForLoop loop = (CompositeTaskForLoop) node;
                Assert.assertEquals(loop.getCollection().getName(), "foobar", "Expecting one loop with a collection called 'foobar'");
                Assert.assertEquals(loop.getVariable().getName(), "item", "Expecting one loop with a variable called 'item'");
            }
        }
        Assert.assertTrue(found, "Expected one top-level loop in the composite task, didn't find any");
    }
}
