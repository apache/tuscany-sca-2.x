package org.apache.tuscany.container.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.container.javascript.mock.Greeting;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * Tests for invoker JavaScriptComponents
 */
public class ScriptInvokeTestCase extends TestCase {

    private static final String SCRIPT = "function greet(name) { return name }";

    private RhinoScript rhinoScript;

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", rhinoScript, services, new HashMap<String, Object>(),
                null, scope, ArtifactFactory.createWireService(), null);
        scope.register(context);
        Greeting object = (Greeting) context.getServiceInstance();
        assertEquals("foo", object.greet("foo"));
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        rhinoScript = new RhinoScript("test", SCRIPT);
    }
}
