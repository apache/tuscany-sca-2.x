package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.groovy.injectors.SingletonInjector;
import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class PropertyTestCase extends MockObjectTestCase {

    private static final String SCRIPT = "import org.apache.tuscany.container.groovy.mock.Greeting;" +
            "class Foo implements Greeting{" +
            "   String property;" +
            "   public String greet(String name){" +
            "       return property;  " +
            "   }" +
            "}";

    /**
     * Tests a basic invocation down a source wire
     */
    public void testPropertyInjection() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        List<PropertyInjector> injectors = new ArrayList<PropertyInjector>();
        injectors.add(new SingletonInjector("property", "bar"));
        GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", PropertyTestCase.SCRIPT,
                services, Scope.MODULE, injectors, null);
        context.setScopeContext(scope);
        Greeting greeting = context.getService();
        assertEquals("bar", greeting.greet("foo"));
        scope.stop();
    }


}
