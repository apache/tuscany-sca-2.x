package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests connecting an inbound system wire to an outbound system wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemInboundtoOutboundTestCase extends MockObjectTestCase {

    public void testWire() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(OutboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        OutboundWire<Target> outboundWire = (OutboundWire<Target>) mockWire.proxy();
        InboundWire<Target> inboundWire = new SystemInboundWire<Target>("service", Target.class);
        inboundWire.setTargetWire(outboundWire);
        assertSame(inboundWire.getTargetService(), target);
    }

}
