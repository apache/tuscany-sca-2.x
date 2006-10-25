package org.apache.tuscany.container.script.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperInstance;
import org.apache.tuscany.container.script.helper.mock.MockInstanceFactory;

public class ScriptHelperInstanceFactoryTestCase extends TestCase {

    public void testCreateInstance() throws InvocationTargetException {
        MockInstanceFactory factory = new MockInstanceFactory("foo.mock", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptHelperInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
    }

    public void testCreateInstanceNoClass() throws InvocationTargetException {
        MockInstanceFactory factory = new MockInstanceFactory("foo.mock", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptHelperInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
    }

    public void testCreateInstanceRuby() throws InvocationTargetException {
        MockInstanceFactory factory = new MockInstanceFactory("foo.mock", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("foo", "bar");
        ScriptHelperInstance instance = factory.createInstance(null, context);
        assertNotNull(instance);
    }

    public void testGetters() throws InvocationTargetException {
        MockInstanceFactory factory = new MockInstanceFactory("foo", getClass().getClassLoader());
        assertEquals("foo", factory.getResourceName());
        assertEquals(getClass().getClassLoader(), factory.getClassLoader());
    }

    public void testGetResponseClasses() {
        MockInstanceFactory factory = new MockInstanceFactory("foo", getClass().getClassLoader());
        Map<String, Class> classes = factory.getResponseClasses(Arrays.asList( new Class[]{ Runnable.class}));
        assertEquals(1, classes.size());
        assertEquals("run", classes.keySet().iterator().next());
        assertEquals(void.class, classes.get("run"));
    }

    protected void setUp() throws Exception {
        super.setUp();
    }
}
