package org.apache.tuscany.container.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.container.javascript.mock.Greeting;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * Tests for component properties
 */
public class PropertyTestCase extends TestCase {

    private static final String SCRIPT = "function greet(name){ return property; }";

    private RhinoScript implClass;

    /**
     * Tests injecting a simple property type on a Groovy implementation instance
     */
    public void testPropertyInjection() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("property", "bar");
        WireService wireService = ArtifactFactory.createWireService();
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass, services, properties, null, scope, wireService, null);
        scope.register(context);
        Greeting greeting = context.getServiceInstance();
        assertEquals("bar", greeting.greet("foo"));
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        implClass = new RhinoScript("test", SCRIPT);
    }
}
