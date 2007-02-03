package org.apache.tuscany.core.wire.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandlerSerializationTestCase extends TestCase {
    private InboundWire wire;
    private WorkContext workContext;

    public void testSerializeDeserialize() throws Exception {
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wire, workContext);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(stream);
        ostream.writeObject(handler);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        SCAExternalizable externalizable = (SCAExternalizable) istream.readObject();

        externalizable.setWorkContext(workContext);
        externalizable.reactivate();
        EasyMock.verify(wire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wire = EasyMock.createMock(InboundWire.class);
        URI uri = URI.create("#foo");
        EasyMock.expect(wire.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getInboundWire(EasyMock.eq("foo"))).andReturn(wire);
        EasyMock.replay(component);
        workContext = new WorkContextImpl();
        workContext.setCurrentAtomicComponent(component);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workContext.setCurrentAtomicComponent(null);
    }
}
