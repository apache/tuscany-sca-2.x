package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemReferenceContextImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests wiring from a system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceWireTestCase extends MockObjectTestCase {

    public void testSReferenceWire() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(InboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        InboundWire<Target> serviceWire = (InboundWire<Target>) mockWire.proxy();
        SystemReferenceContextImpl<Target> referenceContext = new SystemReferenceContextImpl<Target>("reference", Target.class, null);
        referenceContext.setInboundWire(serviceWire);
        OutboundWire<Target> outboundWire = new SystemOutboundWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(serviceWire);
        assertSame(outboundWire.getTargetService(), target); // wires should pass back direct ref
    }

}