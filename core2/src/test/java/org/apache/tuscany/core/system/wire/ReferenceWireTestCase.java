package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemReferenceContextImpl;
import org.apache.tuscany.core.system.wire.SystemReferenceWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceWire;
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
        Mock mockWire = mock(ServiceWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        ServiceWire<Target> serviceWire = (ServiceWire<Target>) mockWire.proxy();
        SystemReferenceContextImpl<Target> referenceContext = new SystemReferenceContextImpl<Target>("reference", Target.class, null);
        referenceContext.setTargetWire(serviceWire);
        ReferenceWire<Target> referenceWire = new SystemReferenceWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        referenceWire.setTargetWire(serviceWire);
        assertSame(referenceWire.getTargetService(), target); // wires should pass back direct ref
    }

}