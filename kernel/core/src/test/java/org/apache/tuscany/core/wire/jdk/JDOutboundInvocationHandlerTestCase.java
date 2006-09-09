package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Proxy;

import org.apache.tuscany.spi.idl.java.JavaServiceContract;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;

/**
 * @version $Rev$ $Date$
 */
public class JDOutboundInvocationHandlerTestCase extends TestCase {

    public void testToString() {
        OutboundWireImpl wire = new OutboundWireImpl();
        wire.setServiceContract(new JavaServiceContract(Foo.class));
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire, new WorkContextImpl());
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        OutboundWireImpl wire = new OutboundWireImpl();
        wire.setServiceContract(new JavaServiceContract(Foo.class));
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire, new WorkContextImpl());
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    private interface Foo {

    }
}
