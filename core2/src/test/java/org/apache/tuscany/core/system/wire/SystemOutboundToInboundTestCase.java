package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.spi.QualifiedName;
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
        Mock mockWire = mock(SystemInboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemInboundWire<Target> inboundWire = (SystemInboundWire<Target>) mockWire.proxy();
        SystemOutboundWire<Target> outboundWire = new SystemOutboundWireImpl<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(inboundWire);
        assertSame(outboundWire.getTargetService(), target);
    }

}