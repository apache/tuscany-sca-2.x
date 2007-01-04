package org.apache.tuscany.core.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
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
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.binding.local.LocalReferenceBinding;
import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractConnectorImplTestCase extends TestCase {
    protected static final String TARGET = "target";
    protected static final QualifiedName TARGET_NAME = new QualifiedName(TARGET);
    protected static final String TARGET_SERVICE = "FooService";
    protected static final QualifiedName TARGET_SERVICE_NAME = new QualifiedName("target/FooService");
    protected static final String RESPONSE = "response";

    protected ConnectorImpl connector;
    protected ServiceContract contract;
    protected Operation<Type> operation;

    protected void setUp() throws Exception {
        super.setUp();
        WireService wireService = new JDKWireService(null, null);
        connector = new ConnectorImpl(wireService, null, null, null);
        contract = new JavaServiceContract(Foo.class);
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

        MockInvoker mockInvoker = new MockInvoker();

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isSystem()).andReturn(false).atLeastOnce();
        target.getInboundWire(EasyMock.eq(TARGET_SERVICE));
        EasyMock.expectLastCall().andReturn(targetWire).atLeastOnce();
        target.createTargetInvoker(EasyMock.eq(TARGET_SERVICE), EasyMock.eq(operation), EasyMock.eq(targetWire));
        EasyMock.expectLastCall().andReturn(mockInvoker);
        EasyMock.replay(target);
        targetWire.setContainer(target);
        return target;
    }

    protected AtomicComponent createAtomicSource(CompositeComponent parent) throws Exception {
        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setTargetName(TARGET_SERVICE_NAME);
        outboundWire.setServiceContract(contract);
        outboundWire.addInvocationChain(operation, outboundChain);

        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put(TARGET_SERVICE, list);

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


    protected Service createService() throws WireConnectException {
        QName qName = new QName("foo", "bar");
        ServiceBinding serviceBinding = new MockServiceBinding();
        InboundInvocationChain targetInboundChain = new InboundInvocationChainImpl(operation);
        targetInboundChain.addInterceptor(new SynchronousBridgingInterceptor());
        InboundWireImpl targetInboundWire = new InboundWireImpl();
        targetInboundWire.setBindingType(qName);
        targetInboundWire.setServiceContract(contract);
        targetInboundWire.addInvocationChain(operation, targetInboundChain);
        targetInboundWire.setContainer(serviceBinding);

        OutboundInvocationChain targetOutboundChain = new OutboundInvocationChainImpl(operation);
        // place an invoker interceptor on the end
        targetOutboundChain.addInterceptor(new InvokerInterceptor());
        OutboundWireImpl targetOutboundWire = new OutboundWireImpl();
        targetOutboundWire.setServiceContract(contract);
        targetOutboundWire.addInvocationChain(operation, targetOutboundChain);
        targetOutboundWire.setContainer(serviceBinding);
        targetOutboundWire.setBindingType(qName);

        serviceBinding.setInboundWire(targetInboundWire);
        serviceBinding.setOutboundWire(targetOutboundWire);
        // manually connect the service chains
        connector.connect(targetInboundChain, targetOutboundChain);
        Service service = new ServiceImpl(TARGET, null, contract);
        service.addServiceBinding(serviceBinding);
        return service;
    }

    /**
     * Creates a service configured with the local binding and places an invoker interceptor on the end of each outbound
     * chain for invocation testing without needing to wire the service to a target
     *
     * @throws org.apache.tuscany.core.builder.WireConnectException
     *
     */
    protected Service createLocalService(CompositeComponent parent) throws WireConnectException {
        LocalServiceBinding serviceBinding = new LocalServiceBinding(TARGET, parent);
        InboundInvocationChain targetInboundChain = new InboundInvocationChainImpl(operation);
        targetInboundChain.addInterceptor(new SynchronousBridgingInterceptor());
        InboundWire targetInboundWire = new InboundWireImpl();
        targetInboundWire.setServiceContract(contract);
        targetInboundWire.addInvocationChain(operation, targetInboundChain);
        targetInboundWire.setContainer(serviceBinding);

        OutboundInvocationChain targetOutboundChain = new OutboundInvocationChainImpl(operation);
        // place an invoker interceptor on the end
        targetOutboundChain.addInterceptor(new InvokerInterceptor());
        OutboundWire targetOutboundWire = new OutboundWireImpl();
        targetOutboundWire.setServiceContract(contract);
        targetOutboundWire.addInvocationChain(operation, targetOutboundChain);
        targetOutboundWire.setContainer(serviceBinding);

        serviceBinding.setInboundWire(targetInboundWire);
        serviceBinding.setOutboundWire(targetOutboundWire);
        // manually connect the service chains
        connector.connect(targetInboundChain, targetOutboundChain);
        Service service = new ServiceImpl(TARGET, null, contract);
        service.addServiceBinding(serviceBinding);
        return service;
    }

    protected ReferenceBinding createLocalReferenceBinding(QualifiedName target)
        throws TargetInvokerCreationException {
        ReferenceBinding referenceBinding = new LocalReferenceBinding("local", null);
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.setContainer(referenceBinding);
        inboundWire.addInvocationChain(operation, inboundChain);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        // Outbound chains always contains at least one interceptor
        outboundChain.addInterceptor(new SynchronousBridgingInterceptor());
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(target);
        outboundWire.addInvocationChain(operation, outboundChain);
        outboundWire.setContainer(referenceBinding);

        referenceBinding.setInboundWire(inboundWire);
        referenceBinding.setOutboundWire(outboundWire);

        return referenceBinding;
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
