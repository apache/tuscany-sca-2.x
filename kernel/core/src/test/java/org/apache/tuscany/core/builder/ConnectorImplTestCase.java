package org.apache.tuscany.core.builder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplTestCase extends TestCase {

    private Connector connector = new ConnectorImpl();

    public void testConnectReferenceWires() {
        // create the wire contract, operation
        ServiceContract contract = new JavaServiceContract(String.class);
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);

        // create source and target interceptors
        Interceptor headInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(headInterceptor);

        // create the inbound wire and chain
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        inboundChain.addInterceptor(EasyMock.isA(BridgingInterceptor.class));
        inboundChain.setTargetInvoker(null);
        inboundChain.prepare();
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(inboundWire.getInvocationChains()).andReturn(inboundChains).atLeastOnce();
        EasyMock.replay(inboundWire);

        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect((outboundWire.getInvocationChains())).andReturn(outboundChains).anyTimes();
        EasyMock.replay(outboundWire);

        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getParent()).andReturn(null);
        EasyMock.expect(reference.createTargetInvoker(contract, operation)).andReturn(null);
        EasyMock.expect(reference.getInboundWire()).andReturn(inboundWire);
        EasyMock.expect(reference.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(reference);

        connector.connect(reference);

        EasyMock.verify(reference);
        EasyMock.verify(inboundWire);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundChain);

    }

    public void testConnectServiceWires() {
        // create the wire contract, operation
        ServiceContract contract = new JavaServiceContract(String.class);
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);

        // create source and target interceptors
        Interceptor headInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(headInterceptor);
        Interceptor tailInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(tailInterceptor);

        // create the inbound wire and chain for the target
        InboundInvocationChain targetChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(targetChain.getOperation()).andReturn(operation).atLeastOnce();
        EasyMock.expect(targetChain.getHeadInterceptor()).andReturn(headInterceptor);
        targetChain.prepare();
        EasyMock.replay(targetChain);
        Map<Operation<?>, InboundInvocationChain> targetChains = new HashMap<Operation<?>, InboundInvocationChain>();
        targetChains.put(operation, targetChain);
        InboundWire targetWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(targetWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(targetWire.getInvocationChains()).andReturn(targetChains);
        targetWire.getSourceCallbackInvocationChains("source");
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(targetWire);

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.MODULE);
        target.getInboundWire(EasyMock.eq("FooService"));
        EasyMock.expectLastCall().andReturn(targetWire).atLeastOnce();
        target.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        // create the inbound wire and chain for the source service
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        inboundChain.addInterceptor(EasyMock.isA(BridgingInterceptor.class));
        inboundChain.setTargetInvoker(null);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(inboundWire.getInvocationChains()).andReturn(inboundChains).atLeastOnce();
        EasyMock.replay(inboundWire);

        // create the outbound wire and chain for the source service
        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getTailInterceptor()).andReturn(tailInterceptor);
        EasyMock.expect(outboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        outboundChain.setTargetInterceptor(headInterceptor);
        outboundChain.prepare();
        outboundChain.setTargetInvoker(null);
        EasyMock.expect(outboundChain.getOperation()).andReturn(operation);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect((outboundWire.getInvocationChains())).andReturn(outboundChains).anyTimes();
        EasyMock.replay(outboundWire);

        // create the service
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("source");
        EasyMock.expect(service.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(service.getInboundWire()).andReturn(inboundWire).atLeastOnce();
        EasyMock.expect(service.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(service.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(service);

        connector.connect(service);

        EasyMock.verify(service);
        EasyMock.verify(inboundWire);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundChain);
    }

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component with one synchronous operation
     */
    public void testConnectAtomicComponentToAtomicComponentSyncWire() throws Exception {
        // create the wire contractm, operation
        ServiceContract contract = new JavaServiceContract(String.class);
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);

        // create source and target interceptors
        Interceptor headInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(headInterceptor);
        Interceptor tailInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(tailInterceptor);

        // create the inbound wire and chain
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire targetWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(targetWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(targetWire.getInvocationChains()).andReturn(inboundChains);
        targetWire.getSourceCallbackInvocationChains("source");
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(targetWire);

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.MODULE);
        target.getInboundWire(EasyMock.eq("FooService"));
        EasyMock.expectLastCall().andReturn(targetWire);
        target.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getTailInterceptor()).andReturn(tailInterceptor);
        EasyMock.expect(outboundChain.getOperation()).andReturn(operation).atLeastOnce();
        outboundChain.setTargetInterceptor(EasyMock.eq(headInterceptor));
        outboundChain.setTargetInvoker(null);
        outboundChain.prepare();
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect((outboundWire.getInvocationChains())).andReturn(outboundChains).anyTimes();
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(outboundWire);
        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put("fooService", list);

        // create the source
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getScope()).andReturn(Scope.MODULE);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(outboundWires);
        EasyMock.expect(source.getName()).andReturn("source");
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(source);

        connector.connect(source);
        EasyMock.verify(headInterceptor);
        EasyMock.verify(tailInterceptor);
        EasyMock.verify(outboundWire);
        EasyMock.verify(targetWire);
        EasyMock.verify(outboundChain);
        EasyMock.verify(inboundChain);
        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    public void testConnectInboundAtomicComponentWires() {

        // create the wire contractm, operation
        ServiceContract contract = new JavaServiceContract(String.class);
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);

        // create the inbound wire and chain
        InboundInvocationChain chain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(chain.getOperation()).andReturn(operation).atLeastOnce();
        chain.setTargetInvoker(null);
        chain.prepare();
        EasyMock.replay(chain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, chain);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceName()).andReturn("FooService");
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(wire.getInvocationChains()).andReturn(inboundChains);
        EasyMock.replay(wire);

        Map<String, InboundWire> wires = new HashMap<String, InboundWire>();
        wires.put("FooService", wire);

        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getParent()).andReturn(null);
        source.getOutboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(wires);
        source.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(source);

        connector.connect(source);

        EasyMock.verify(source);
        EasyMock.verify(wire);
        EasyMock.verify(chain);

    }
}
