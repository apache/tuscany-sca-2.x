package org.apache.tuscany.core.implementation.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.InvalidAutowireInterface;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.TestUtils;
import org.easymock.EasyMock;

/**
 * Verfies specific autowire resolution scenarios
 *
 * @version $Rev$ $Date$
 */
public class AutowireRegistrationTestCase extends TestCase {

    public void testInvalidComponentInterfaceAutowire() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        InboundWire wire = TestUtils.createInboundWire(Foo.class);
        wire.setContainer(parent);
        Map<String, InboundWire> wires = new HashMap<String, InboundWire>();
        wires.put("foo", wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Bar.class);
        component.getServiceInterfaces();
        EasyMock.expectLastCall().andReturn(interfaces);
        EasyMock.replay(component);
        CompositeComponent compoosite = new CompositeComponentImpl("test", parent, null, null);
        try {
            compoosite.register(component);
            fail();
        } catch (InvalidAutowireInterface e) {
            // expected
        }
    }

    public void testInvalidSystemComponentInterfaceAutowire() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        InboundWire wire = TestUtils.createInboundWire(Foo.class);
        wire.setContainer(parent);
        Map<String, InboundWire> wires = new HashMap<String, InboundWire>();
        wires.put("foo", wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Bar.class);
        component.getServiceInterfaces();
        EasyMock.expectLastCall().andReturn(interfaces);
        EasyMock.replay(component);
        CompositeComponent compoosite = new CompositeComponentImpl("test", parent, null, null);
        try {
            compoosite.register(component);
            fail();
        } catch (InvalidAutowireInterface e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static interface Foo {
    }

    public static interface Bar {
    }
}
