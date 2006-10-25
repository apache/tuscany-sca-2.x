package org.apache.tuscany.container.script;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.bsf.BSFManager;
import org.apache.tuscany.container.script.mock.MockBSFEngine;
import org.apache.tuscany.spi.ObjectCreationException;

public class ScriptInstanceFactoryTestCase extends TestCase {

    public void testCreateInstance() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("mock", MockBSFEngine.class.getName(), new String[] {"mock"});
        ScriptInstanceFactory factory = new ScriptInstanceFactory("foo.mock", "bar", "baz", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
//        assertNotNull(instance.clazz);
    }

    public void testCreateInstanceNoClass() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("mock", MockBSFEngine.class.getName(), new String[] {"mock"});
        ScriptInstanceFactory factory = new ScriptInstanceFactory("foo.mock", null, "baz", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
    }

    public void testCreateInstanceRuby() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("ruby", MockBSFEngine.class.getName(), new String[] {"mock"});
        ScriptInstanceFactory factory = new ScriptInstanceFactory("foo.mock", "bar", "baz", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
//        assertNotNull(instance.clazz);
    }

    public void testBadCreateInstance() throws InvocationTargetException {
        ScriptInstanceFactory factory = new ScriptInstanceFactory("foo", "bar", "baz", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        try {
            factory.createInstance(null, context);
            fail();
        } catch (ObjectCreationException e) {
            // expected
        }
    }

    public void testGetters() throws InvocationTargetException {
        ScriptInstanceFactory factory = new ScriptInstanceFactory("foo", "bar", "baz", getClass().getClassLoader());
//        assertEquals("foo", factory.getScriptName());
//        assertEquals("bar", factory.getClassName());
//        assertEquals("baz", factory.getScriptSource());
        assertEquals(getClass().getClassLoader(), factory.getClassLoader());
    }

    protected void setUp() throws Exception {
        super.setUp();
    }
}
