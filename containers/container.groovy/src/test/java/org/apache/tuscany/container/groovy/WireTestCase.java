package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.test.ArtifactFactory;
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
            "   " +
            "   void setWire(Greeting ref){" +
            "       wire = ref;" +
            "   };" +
            "   " +
            "   String greet(String name){" +
            "       return wire.greet(name);  " +
            "   };" +
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
        GroovyAtomicComponent<Greeting> context = new GroovyAtomicComponent<Greeting>("source", SCRIPT,
                services, Scope.MODULE, null, null, scope, ArtifactFactory.createWireService());
        OutboundWire<?> wire = ArtifactFactory.createOutboundWire("wire", Greeting.class);
        ArtifactFactory.terminateWire(wire);
        Mock mock = mock(TargetInvoker.class);
        mock.expects(atLeastOnce()).method("isCacheable").will(returnValue(false));
        mock.expects(atLeastOnce()).method("invoke").will(new Stub() {
            public Object invoke(Invocation invocation) throws Throwable {
                Message msg = new MessageImpl();
                msg.setBody("foo");
                return msg;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(invoker);
        }
        scope.register(context);
        context.addOutboundWire(wire);
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
        GroovyAtomicComponent<Greeting> context = new GroovyAtomicComponent<Greeting>("source", SCRIPT2, services,
                Scope.MODULE, null, null, scope, ArtifactFactory.createWireService());
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
        GroovyAtomicComponent<Greeting> context = new GroovyAtomicComponent<Greeting>("source", SCRIPT2,
                services, Scope.MODULE, null, null, scope, ArtifactFactory.createWireService());
        scope.register(context);

        InboundWire<?> wire = ArtifactFactory.createInboundWire("Greeting", Greeting.class);
        ArtifactFactory.terminateWire(wire);
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker("Greeting", chain.getMethod()));
        }
        context.addInboundWire(wire);
        Greeting greeting = (Greeting) context.getServiceInstance("Greeting");
        assertEquals("foo", greeting.greet("foo"));
        scope.stop();
    }

}
