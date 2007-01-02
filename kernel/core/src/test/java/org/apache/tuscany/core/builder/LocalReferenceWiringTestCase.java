package org.apache.tuscany.core.builder;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Verifies various wiring "scenarios" or paths through the connector
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceWiringTestCase extends AbstractLocalWiringTestCase {
    private Service service;
    private Reference reference;

    /**
     * Verifies the case where inbound and outbound reference wires are connected followed by the outbound reference
     * wire being connected to a target. This wiring scenario occurs when a reference is configured with the local
     * binding.
     */
    public void testConnectLocalReferenceBindingToCompositeService() throws Exception {
        createLocalReferenceToServiceConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectLocalReferenceBindingToCompositeServiceNoMatchingBinding() throws Exception {
        createLocalReferenceToInvalidServiceConfiguration();
        try {
            connector.connect(reference);
            fail();
        } catch (NoCompatibleBindingsException e) {
            // expected
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    private void createLocalReferenceToServiceConfiguration() throws Exception {
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createLocalService(topComposite);
        reference = createLocalReference(parent);
    }

    private void createLocalReferenceToInvalidServiceConfiguration() throws Exception {
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createService(topComposite);
        reference = createLocalReference(parent);
    }

    protected Service createService(CompositeComponent parent) throws WireConnectException {
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


}
