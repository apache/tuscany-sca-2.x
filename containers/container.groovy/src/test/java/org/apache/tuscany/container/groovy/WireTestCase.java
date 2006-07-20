package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import org.easymock.IArgumentMatcher;

import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.test.ArtifactFactory;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * @version $$Rev$$ $$Date$$
 */
public class WireTestCase extends TestCase {
    private static final List<PropertyInjector> INJECTORS = Collections.emptyList();

    private static final String SCRIPT = "import org.apache.tuscany.container.groovy.mock.Greeting;"
            + "class Foo implements Greeting{"
            + "   Greeting wire;"
            + "   "
            + "   void setWire(Greeting ref){"
            + "       wire = ref;"
            + "   };"
            + "   "
            + "   String greet(String name){"
            + "       return wire.greet(name);  "
            + "   };"
            + "}";

    private static final String SCRIPT2 = "import org.apache.tuscany.container.groovy.mock.Greeting;"
            + "class Foo implements Greeting{"
            + "   public String greet(String name){"
            + "       return name;  "
            + "   }"
            + "}";

    private Class<? extends GroovyObject> implClass1;
    private Class<? extends GroovyObject> implClass2;

    /**
     * Tests a basic invocation down a source wire
     */
    public void testReferenceWireInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();

        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicComponent<Greeting> context =
                new GroovyAtomicComponent<Greeting>("source",
                                                    implClass1,
                                                    services,
                                                    INJECTORS,
                                                    null,
                                                    scope,
                                                    ArtifactFactory.createWireService());
        OutboundWire<?> wire = ArtifactFactory.createOutboundWire("wire", Greeting.class);
        ArtifactFactory.terminateWire(wire);

        TargetInvoker invoker = createMock(TargetInvoker.class);
        expect(invoker.isCacheable()).andReturn(false);
        Message response = new MessageImpl();
        response.setBody("foo");
        expect(invoker.invoke(eqMessage())).andReturn(response);
        replay(invoker);

        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(invoker);
        }
        scope.register(context);
        context.addOutboundWire(wire);
        Greeting greeting = context.getServiceInstance();
        assertEquals("foo", greeting.greet("foo"));
        verify(invoker);

        scope.stop();
    }

    // todo this could be generalized and moved to test module
    public static Message eqMessage() {
        reportMatcher(new IArgumentMatcher() {
            public boolean matches(Object object) {
                if (!(object instanceof Message)) {
                    return false;
                }
                final Message msg = (Message) object;
                Object[] body = (Object[]) msg.getBody();
                return "foo".equals(body[0]);
            }

            public void appendTo(StringBuffer stringBuffer) {
            }
        });
        return null;
    }


    /**
     * Tests a basic invocation to a target
     */
    public void testTargetInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicComponent<Greeting> context =
                new GroovyAtomicComponent<Greeting>("source",
                                                    implClass2,
                                                    services,
                                                    INJECTORS,
                                                    null,
                                                    scope,
                                                    ArtifactFactory.createWireService());
        scope.register(context);
        TargetInvoker invoker =
                context.createTargetInvoker("greeting", Greeting.class.getMethod("greet", String.class));
        assertEquals("foo", invoker.invokeTarget(new String[]{"foo"}));
        scope.stop();
    }


    /**
     * Tests a basic invocation down a target wire
     */
    public void testTargetWireInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicComponent<Greeting> context =
                new GroovyAtomicComponent<Greeting>("source",
                                                    implClass2,
                                                    services,
                                                    INJECTORS,
                                                    null,
                                                    scope,
                                                    ArtifactFactory.createWireService());
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

    protected void setUp() throws Exception {
        super.setUp();
        GroovyClassLoader cl = new GroovyClassLoader(getClass().getClassLoader());
        implClass1 = cl.parseClass(SCRIPT);
        implClass2 = cl.parseClass(SCRIPT2);
    }
}
