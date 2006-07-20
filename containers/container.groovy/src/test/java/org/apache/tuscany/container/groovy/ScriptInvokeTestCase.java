package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyClassLoader;
import junit.framework.TestCase;

import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScriptInvokeTestCase extends TestCase {

    private static final String SCRIPT = "def greet(name) { return name }";
    private static final List<PropertyInjector> INJECTORS = Collections.emptyList();

    private Class<? extends GroovyObject> implClass;

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicComponent<GroovyObject> context =
                new GroovyAtomicComponent<GroovyObject>("source",
                                                        implClass,
                                                        services,
                                                        INJECTORS,
                                                        null,
                                                        scope,
                                                        ArtifactFactory.createWireService());
        scope.register(context);
        GroovyObject object = context.getServiceInstance();
        assertEquals("foo", object.invokeMethod("greet", "foo"));
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        GroovyClassLoader cl = new GroovyClassLoader(getClass().getClassLoader());
        implClass = cl.parseClass(SCRIPT);
    }
}
