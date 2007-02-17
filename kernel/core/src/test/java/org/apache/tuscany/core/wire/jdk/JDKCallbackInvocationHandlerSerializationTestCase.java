package org.apache.tuscany.core.wire.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandlerSerializationTestCase extends TestCase {
    private WorkContext workContext;
    private List<Wire> wires;
    private AtomicComponent component;

    public void testSerializeDeserialize() throws Exception {
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wires, workContext);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(stream);
        ostream.writeObject(handler);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        SCAExternalizable externalizable = (SCAExternalizable) istream.readObject();

        externalizable.setWorkContext(workContext);
        externalizable.reactivate();
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        URI uri = URI.create("#foo");
        Wire wire = new WireImpl();
        wire.setSourceUri(uri);
        wires = new ArrayList<Wire>();
        wires.add(wire);
        List<Wire> wireList = new ArrayList<Wire>();
        wireList.add(wire);
        component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getWires("foo")).andReturn(wireList);
        EasyMock.replay(component);
        workContext = new WorkContextImpl();
        workContext.setCurrentAtomicComponent(component);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workContext.setCurrentAtomicComponent(null);
    }
}
