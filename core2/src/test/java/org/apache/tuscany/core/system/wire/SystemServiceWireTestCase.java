package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceWireTestCase extends MockObjectTestCase {

    public void testWireResolution() throws NoSuchMethodException {
        Target originalTarget = new TargetImpl();
        // construct mock target
        Mock mock = mock(ComponentContext.class);
        mock.expects(once()).method("getService").will(returnValue(originalTarget));
        ComponentContext<Target> context = (ComponentContext<Target>) mock.proxy();
        InboundWire<Target> inboundWire = new SystemInboundWire<Target>("Target", Target.class, context);

        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", inboundWire, null);
        Target target = serviceContext.getService();
        assertSame(target, originalTarget);
    }
}
