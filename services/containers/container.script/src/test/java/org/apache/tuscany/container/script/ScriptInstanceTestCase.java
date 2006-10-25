package org.apache.tuscany.container.script;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.mock.MockBSFEngine;

public class ScriptInstanceTestCase extends TestCase {

    private ScriptInstance instance;

    public void testInvokeTarget() throws InvocationTargetException {
        assertEquals("hello:", instance.invokeTarget("hello", null));
    }

    public void testInvokeTargetException() throws InvocationTargetException {
        try {
            instance.invokeTarget("bang", null);
            fail();
        } catch (InvocationTargetException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.instance = new ScriptInstance(new MockBSFEngine(), null);
    }
}
