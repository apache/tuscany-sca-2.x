package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.component.SystemService;
import org.apache.tuscany.core.system.component.SystemServiceImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies that a system context interacts correctly with configured, connected inbound and outbound system
 * wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceComponentWireTestCase extends MockObjectTestCase {

    public void testServiceContext() throws NoSuchMethodException {
        Target target = new TargetImpl();
        Mock mockWire = mock(SystemOutboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemOutboundWire<Target> outboundWire = (SystemOutboundWire<Target>) mockWire.proxy();

        SystemInboundWire<Target> wire = new SystemInboundWireImpl<Target>("Target", Target.class);
        SystemService<Target> serviceContext = new SystemServiceImpl<Target>("service", null);
        serviceContext.setInboundWire(wire);
        serviceContext.setOutboundWire(outboundWire);
        wire.setTargetWire(outboundWire);
        assertSame(target, serviceContext.getService());
    }
}
