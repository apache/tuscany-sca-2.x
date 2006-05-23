package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTestCase extends MockObjectTestCase {

    private static final String SCRIPT = "import org.apache.tuscany.container.groovy.mock.Greeting;" +
            "class Foo implements Greeting{" +
            "   Greeting wire;" +
            "   public String greet(String name){" +
            "       return wire.greet(name);  " +
            "   }" +
            "}";

    private static final String SCRIPT2 = "import org.apache.tuscany.container.groovy.mock.Greeting;" +
            "class Foo implements Greeting{" +
            "   public String greet(String name){" +
            "       return name;  " +
            "   }" +
            "}";

    /**
     * Tests a basic invocation down a source wire
     */
    public void testSourceWireInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", SCRIPT, services, Scope.MODULE,null, null);
        context.setScopeContext(scope);
        Mock mock = mock(SourceWire.class);
        mock.expects(atLeastOnce()).method("getTargetService").will(
                returnValue(new Greeting() {
                    public String greet(String name) {
                        return name;
                    }
                }));
        mock.expects(atLeastOnce()).method("getReferenceName").will(returnValue("wire"));
        SourceWire<Greeting> wire = (SourceWire<Greeting>) mock.proxy();
        context.addSourceWire(wire);
        Greeting greeting = context.getService();
        assertEquals("foo", greeting.greet("foo"));
        scope.stop();
    }


    /**
     * Tests a basic invocation to a target
     */
    public void testTargetInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", SCRIPT2, services,
                Scope.MODULE,null, null);
        context.setScopeContext(scope);
        TargetInvoker invoker = context.createTargetInvoker("greeting",Greeting.class.getMethod("greet", String.class));
        assertEquals("foo", invoker.invokeTarget(new String[]{"foo"}));
        scope.stop();
    }

}
