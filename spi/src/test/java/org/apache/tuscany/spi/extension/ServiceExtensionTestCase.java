package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class ServiceExtensionTestCase extends TestCase {

    public void testScope() throws Exception {
        ServiceExtension<Object> service = new ServiceExtension<Object>(null, null, null, null);
        assertEquals(Scope.COMPOSITE, service.getScope());
    }

    @SuppressWarnings("unchecked")
    public void testSetGetInterface() throws Exception {
        InboundWire wire = createMock(InboundWire.class);
        wire.getBusinessInterface();
        expectLastCall().andReturn(getClass());
        replay(wire);
        ServiceExtension<?> service = new ServiceExtension(null, null, null, null);
        service.setInboundWire(wire);
        service.getInterface();
    }


    public void testPrepare() throws Exception {
        ServiceExtension<Object> service = new ServiceExtension<Object>(null, null, null, null);
        service.prepare();
    }

}
