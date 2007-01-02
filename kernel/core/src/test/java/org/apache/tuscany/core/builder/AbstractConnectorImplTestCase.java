package org.apache.tuscany.core.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractConnectorImplTestCase extends TestCase {
    protected static final String FOO_SERVICE = "FooService";
    protected static final QualifiedName FOO_TARGET = new QualifiedName("target/FooService");
    protected static final String RESPONSE = "response";

    protected ConnectorImpl connector;
    protected ServiceContract contract;
    protected Operation<Type> operation;

    protected void setUp() throws Exception {
        super.setUp();
        connector = new ConnectorImpl();
        contract = new JavaServiceContract(AbstractConnectorImplTestCase.Foo.class);
        operation = new Operation<Type>("bar", null, null, null);
    }

    protected interface Foo {
        String echo();
    }

    protected AtomicComponent createAtomicTarget() throws Exception {
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new InvokerInterceptor());
        InboundWire targetWire = new InboundWireImpl();
        targetWire.setServiceContract(contract);
        targetWire.addInvocationChain(operation, chain);

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isSystem()).andReturn(false).atLeastOnce();
        target.getInboundWire(EasyMock.eq(FOO_SERVICE));
        EasyMock.expectLastCall().andReturn(targetWire).atLeastOnce();
        target.createTargetInvoker(EasyMock.eq(FOO_SERVICE), EasyMock.eq(operation), EasyMock.eq(targetWire));
        AbstractConnectorImplTestCase.MockInvoker mockInvoker = new AbstractConnectorImplTestCase.MockInvoker();
        EasyMock.expectLastCall().andReturn(mockInvoker);
        EasyMock.replay(target);
        targetWire.setContainer(target);
        return target;
    }

    protected AtomicComponent createAtomicSource(CompositeComponent parent) throws Exception {
        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setTargetName(FOO_TARGET);
        outboundWire.setServiceContract(contract);
        outboundWire.addInvocationChain(operation, outboundChain);

        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put(FOO_SERVICE, list);

        // create the source
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(outboundWires).atLeastOnce();
        EasyMock.expect(source.getName()).andReturn("source").atLeastOnce();
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyList());
        EasyMock.replay(source);

        outboundWire.setContainer(source);
        return source;
    }

    protected static class MockInvoker implements TargetInvoker {
        public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
            return null;
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            Message resp = new MessageImpl();
            resp.setBody(RESPONSE);
            return resp;
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public boolean isOptimizable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    protected static class MockInterceptor implements Interceptor {
        private Interceptor next;
        private boolean invoked;

        public Message invoke(Message msg) {
            invoked = true;
            return next.invoke(msg);
        }

        public void setNext(Interceptor next) {
            this.next = next;
        }

        public Interceptor getNext() {
            return next;
        }

        public boolean isInvoked() {
            return invoked;
        }

        public boolean isOptimizable() {
            return false;
        }
    }

}
