package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceTestCase extends TestCase {

    public void testScope() throws Exception {
        TestReference ref = new TestReference(null, null, null);
        assertEquals(Scope.COMPOSITE, ref.getScope());

    }

    public void testSetGetInterface() throws Exception {
        TestReference<TestReference> ref = new TestReference<TestReference>(null, null, null);
        ref.setInterface(TestReference.class);
        assertEquals(TestReference.class, ref.getInterface());

    }

    public void testPrepare() throws Exception {
        Method method = getClass().getMethod("testPrepare");
        InboundInvocationChain chain = createMock(InboundInvocationChain.class);
        chain.setTargetInvoker(null);
        expectLastCall();
        chain.getMethod();
        expectLastCall().andReturn(method);
        chain.prepare();
        expectLastCall();
        InboundWire wire = createMock(InboundWire.class);
        wire.getInvocationChains();
        Map<Method, InvocationChain> chains = new HashMap<Method, InvocationChain>();
        chains.put(method, chain);
        expectLastCall().andReturn(chains);
        OutboundWire outboundWire = createMock(OutboundWire.class);
        outboundWire.getTargetName();
        expectLastCall().andReturn(new QualifiedName("foo/bar"));
        replay(chain);
        replay(wire);
        replay(outboundWire);
        TestReference<?> ref = new TestReference(null, null, null);
        ref.setInboundWire(wire);
        ref.setOutboundWire(outboundWire);
        ref.prepare();
    }

    private class TestReference<T> extends ReferenceExtension<T> {
        public TestReference(String name, CompositeComponent parent, WireService wireService) {
            super(name, parent, wireService);
        }

        public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
            return null;
        }
    }
}
