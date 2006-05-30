package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies that a system context interacts correctly with configured, connected inbound and outbound system
 * wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextWireTestCase extends MockObjectTestCase {

    public void testServiceContext() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(OutboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        OutboundWire<Target> outboundWire = (OutboundWire<Target>) mockWire.proxy();

        SystemInboundWire<Target> wire = new SystemInboundWire<Target>("Target", Target.class);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", null);
        serviceContext.setInboundWire(wire);
        serviceContext.setOutboundWire(outboundWire);
        wire.setTargetWire(outboundWire);
        assertSame(target, serviceContext.getService());
    }
}
