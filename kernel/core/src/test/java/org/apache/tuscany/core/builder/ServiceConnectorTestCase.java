package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ServiceConnectorTestCase extends AbstractConnectorImplTestCase {
    private AtomicComponent atomicTarget;
    private CompositeComponent parent;
    private CompositeComponent compositeTarget;
    private InboundInvocationChain inboundChain;
    private ServiceBinding sourceServiceBinding;
    private Reference referenceTarget;

    public void testConnectServiceToAtomicComponent() throws Exception {
        configureAtomicTarget();
        Service sourceService = new ServiceImpl("foo", parent, contract);
        sourceService.addServiceBinding(sourceServiceBinding);
        connector.connect(sourceService);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectServiceToChildCompositeService() throws Exception {
        configureChildCompositeServiceTarget();
        Service sourceService = new ServiceImpl("foo", parent, contract);
        sourceService.addServiceBinding(sourceServiceBinding);
        connector.connect(sourceService);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectServiceToReference() throws Exception {
        configureReferenceTarget();
        Service sourceService = new ServiceImpl("foo", parent, contract);
        sourceService.addServiceBinding(sourceServiceBinding);
        connector.connect(sourceService);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
        inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        // Outbound chains always contains at least one interceptor
        outboundChain.addInterceptor(new SynchronousBridgingInterceptor());
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(TARGET_SERVICE_NAME);
        outboundWire.addInvocationChain(operation, outboundChain);

        sourceServiceBinding = new MockServiceBinding();
        sourceServiceBinding.setInboundWire(inboundWire);
        sourceServiceBinding.setOutboundWire(outboundWire);
        inboundWire.setContainer(sourceServiceBinding);
        outboundWire.setContainer(sourceServiceBinding);

    }

    private void configureAtomicTarget() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new InvokerInterceptor());
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);

        atomicTarget = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(atomicTarget.getInboundWire(EasyMock.isA(String.class))).andReturn(inboundWire).atLeastOnce();
        EasyMock.expect(atomicTarget.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(atomicTarget.createTargetInvoker(EasyMock.isA(String.class),
            EasyMock.isA(Operation.class),
            EasyMock.isA(InboundWire.class))).andReturn(new MockInvoker());
        EasyMock.replay(atomicTarget);

        inboundWire.setContainer(atomicTarget);

        parent = EasyMock.createNiceMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild(TARGET)).andReturn(atomicTarget);
        EasyMock.replay(parent);
    }

    private void configureChildCompositeServiceTarget() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new InvokerInterceptor());
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);

        compositeTarget = EasyMock.createMock(CompositeComponent.class);
        Service service = createLocalService(compositeTarget);
        EasyMock.expect(compositeTarget.getService(TARGET_SERVICE)).andReturn(service);
        EasyMock.replay(compositeTarget);

        inboundWire.setContainer(compositeTarget);

        parent = EasyMock.createNiceMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild(TARGET)).andReturn(compositeTarget);
        EasyMock.replay(parent);
    }


    private void configureReferenceTarget() throws Exception {
        ReferenceBinding binding = createLocalReferenceBinding(TARGET_NAME);
        referenceTarget = new ReferenceImpl(TARGET, parent, contract);
        referenceTarget.addReferenceBinding(binding);
        // put a terminating interceptor on the outbound wire of the reference for testing an invocation
        binding.getOutboundWire().getInvocationChains().get(operation).addInterceptor(new InvokerInterceptor());
        connector.connect(binding.getInboundWire(), binding.getOutboundWire(), true);
        parent = EasyMock.createNiceMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild(TARGET)).andReturn(referenceTarget);
        EasyMock.replay(parent);
    }

}
