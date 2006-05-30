package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests connecting an outbound system wire to an inbound system wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemOutboundToInboundTestCase extends MockObjectTestCase {

    public void testWire() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(InboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        InboundWire<Target> inboundWire = (InboundWire<Target>) mockWire.proxy();
        OutboundWire<Target> outboundWire = new SystemOutboundWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(inboundWire);
        assertSame(outboundWire.getTargetService(), target);
    }

}