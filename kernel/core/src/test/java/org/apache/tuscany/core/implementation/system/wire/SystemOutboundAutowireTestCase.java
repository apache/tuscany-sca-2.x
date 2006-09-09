package org.apache.tuscany.core.implementation.system.wire;

import org.apache.tuscany.spi.component.TargetNotFoundException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.AutowireComponent;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class SystemOutboundAutowireTestCase extends TestCase {

    public void testAutowire() {
        AutowireComponent<?> component = createMock(AutowireComponent.class);
        expect(component.resolveInstance(Object.class)).andReturn(new Object());
        replay(component);
        SystemOutboundAutowire<Object> wire = new SystemOutboundAutowire<Object>("foo", Object.class, component, false);
        assertNotNull(wire.getTargetService());
        verify(component);
    }


    public void testNonExistentAutowire() {
        AutowireComponent<?> component = createMock(AutowireComponent.class);
        expect(component.resolveInstance(Object.class)).andReturn(null);
        replay(component);
        SystemOutboundAutowire<Object> wire = new SystemOutboundAutowire<Object>("foo", Object.class, component, true);
        try {
            wire.getTargetService();
            fail();
        } catch (TargetNotFoundException e) {
            // expected
        }
        verify(component);
    }


    public void testNonExistentAutowireNotRequired() {
        AutowireComponent<?> component = createMock(AutowireComponent.class);
        expect(component.resolveInstance(Object.class)).andReturn(null);
        replay(component);
        SystemOutboundAutowire<Object> wire = new SystemOutboundAutowire<Object>("foo", Object.class, component, false);
        try {
            assertNull(wire.getTargetService());
        } catch (TargetNotFoundException e) {
            fail();
        }
        verify(component);
    }


}
