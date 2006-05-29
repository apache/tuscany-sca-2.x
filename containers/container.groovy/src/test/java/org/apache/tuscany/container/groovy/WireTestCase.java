package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.InboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
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
    public void testReferenceWireInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", SCRIPT, services,
                Scope.MODULE, null, null, scope);
        scope.register(context);
        Mock mock = mock(OutboundWire.class);
        mock.expects(atLeastOnce()).method("getTargetService").will(
                returnValue(new Greeting() {
                    public String greet(String name) {
                        return name;
                    }
                }));
        mock.expects(atLeastOnce()).method("getReferenceName").will(returnValue("wire"));
        OutboundWire<Greeting> wire = (OutboundWire<Greeting>) mock.proxy();
        context.addReferenceWire(wire);
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
                Scope.MODULE, null, null, scope);
        scope.register(context);
        TargetInvoker invoker = context.createTargetInvoker("greeting", Greeting.class.getMethod("greet", String.class));
        assertEquals("foo", invoker.invokeTarget(new String[]{"foo"}));
        scope.stop();
    }


    /**
     * Tests a basic invocation down a target wire
     */
    public void testTargetWireInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        final GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", SCRIPT2,
                services, Scope.MODULE, null, null, scope);
        scope.register(context);
        Mock mock = mock(InboundWire.class);
        mock.stubs().method("getServiceName").will(returnValue("Greeting"));
        mock.expects(atLeastOnce()).method("getTargetService").will(
                new Stub() {
                    public Object invoke(Invocation invocation) throws Throwable {
                        return context.getTargetInstance();
                    }

                    public StringBuffer describeTo(StringBuffer buff) {
                        return buff.append("returns the target instance");
                    }
                });

        InboundWire<Greeting> wire = (InboundWire<Greeting>) mock.proxy();
        context.addServiceWire(wire);
        Greeting greeting = (Greeting) context.getService("Greeting");
        assertEquals("foo", greeting.greet("foo"));
        scope.stop();
    }

}
