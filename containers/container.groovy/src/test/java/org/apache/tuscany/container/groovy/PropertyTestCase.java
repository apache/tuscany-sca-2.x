package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.groovy.injectors.SingletonInjector;
import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.test.ArtifactFactory;
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
     * Tests injecting a simple property type on a Groovy implementation instance
     */
    public void testPropertyInjection() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        List<PropertyInjector> injectors = new ArrayList<PropertyInjector>();
        injectors.add(new SingletonInjector("property", "bar"));
        GroovyAtomicComponent<Greeting> context = new GroovyAtomicComponent<Greeting>("source", PropertyTestCase.SCRIPT,
                services, Scope.MODULE, injectors, null, scope, ArtifactFactory.createWireService());
        scope.register(context);
        Greeting greeting = context.getService();
        assertEquals("bar", greeting.greet("foo"));
        scope.stop();
    }


}
