package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
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
        Mock mockWire = mock(SystemOutboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemOutboundWire<Target> outboundWire = (SystemOutboundWire<Target>) mockWire.proxy();
        SystemInboundWire<Target> inboundWire = new SystemInboundWireImpl<Target>("service", Target.class);
        inboundWire.setTargetWire(outboundWire);
        assertSame(inboundWire.getTargetService(), target);
    }

}
