package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.binding.local.LocalReferenceBinding;
import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;

/**
 * Verifies various wiring "scenarios" or paths through the connector
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractLocalWiringTestCase extends AbstractConnectorImplTestCase {
    protected ReferenceBinding referenceBinding;


    protected void setUp() throws Exception {
        super.setUp();
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

    protected Reference createLocalReference(CompositeComponent parent, QualifiedName target) throws Exception {
        ReferenceBinding referenceBinding = createLocalReferenceBinding(target);
        Reference reference = new ReferenceImpl("foo", parent, contract);
        reference.addReferenceBinding(referenceBinding);
        return reference;
    }

    protected ReferenceBinding createLocalReferenceBinding(QualifiedName target)
        throws TargetInvokerCreationException {
        referenceBinding = new LocalReferenceBinding("local", null);
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

}
