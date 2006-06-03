package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.GroovyObject;
import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScriptInvokeTestCase extends MockObjectTestCase {

    private String script2 = "def greet(name) { return name }";

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicComponent<GroovyObject> context = new GroovyAtomicComponent<GroovyObject>("source", script2,
                services, Scope.MODULE, null, null,scope, ArtifactFactory.createWireService());
        scope.register(context);
        GroovyObject object = context.getService();
        assertEquals("foo", object.invokeMethod("greet", "foo"));
        scope.stop();
    }

}
