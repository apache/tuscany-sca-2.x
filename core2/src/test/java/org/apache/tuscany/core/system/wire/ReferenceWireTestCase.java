package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemReferenceContextImpl;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
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
        Mock mockWire = mock(TargetWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        TargetWire<Target> targetWire = (TargetWire<Target>) mockWire.proxy();
        SystemReferenceContextImpl<Target> referenceContext = new SystemReferenceContextImpl<Target>("reference", Target.class, null);
        referenceContext.setTargetWire(targetWire);
        SourceWire<Target> sourceWire = new SystemSourceWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        sourceWire.setTargetWire(targetWire);
        assertSame(sourceWire.getTargetService(), target); // wires should pass back direct ref
    }

}