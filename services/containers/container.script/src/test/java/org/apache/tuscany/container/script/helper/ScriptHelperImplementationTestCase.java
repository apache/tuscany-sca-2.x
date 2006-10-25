package org.apache.tuscany.container.script.helper;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperImplementation;
import org.apache.tuscany.container.script.helper.ScriptHelperInstanceFactory;
import org.apache.tuscany.container.script.helper.mock.MockInstanceFactory;

public class ScriptHelperImplementationTestCase extends TestCase {

    private ScriptHelperInstanceFactory bsfEasy;

    public void testGetBSFEasy() {
        ScriptHelperImplementation impl = new ScriptHelperImplementation();
        impl.setScriptInstanceFactory(bsfEasy);
        assertEquals(bsfEasy, impl.getScriptInstanceFactory());
    }

    public void testGetResourceName() {
        ScriptHelperImplementation impl = new ScriptHelperImplementation();
        impl.setResourceName("foo");
        assertEquals("foo", impl.getResourceName());
    }

    public void setUp() throws Exception {
        super.setUp();
        bsfEasy = new MockInstanceFactory("BSFEasyTestCase", null);
    }

}
