package org.apache.tuscany.core.builder;

import java.util.Collections;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Verifies various wiring "scenarios" or paths through the connector
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceWiringTestCase extends AbstractConnectorImplTestCase {
    protected ReferenceBinding referenceBinding;
    private Service service;
    private Reference reference;
    private AtomicComponent atomicComponent;

    /**
     * Verifies the case where inbound and outbound reference wires are connected followed by the outbound reference
     * wire being connected to a target that is an atomic component and child of the reference's parent composite. This
     * wiring scenario occurs when a reference is configured with the local binding.
     */
    public void testConnectLocalReferenceBindingToAtomicComponentService() throws Exception {
        createLocalReferenceToSiblingAtomicConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

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

    /**
     * Verifies a connection to a service offered by a sibling composite of the reference's parent
     *
     * @throws Exception
     */
    public void testConnectLocalReferenceBindingToSiblingCompositeService() throws Exception {
        createLocalReferenceToSiblingCompositeServiceConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectLocalReferenceBindingToSiblingCompositeServiceNoMatchingBinding() throws Exception {
        createLocalReferenceToSiblingCompositeServiceConfigurationNoMatchingBinding();
        try {
            connector.connect(reference);
            fail();
        } catch (TargetServiceNotFoundException e) {
            // expected
        }
    }

    public void testConnectLocalReferenceBindingToInvalidTarget() throws Exception {
        createLocalReferenceToInvalidTarget();
        try {
            connector.connect(reference);
            fail();
        } catch (InvalidTargetTypeException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void createLocalReferenceToServiceConfiguration() throws Exception {
        final CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);

        topComposite.getInboundWire(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return createLocalInboundWire(topComposite);
            }
        });
        service = createLocalService(topComposite);
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

        //service = createLocalService(topComposite);
        reference = createLocalReference(parent, TARGET_NAME);
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

        service = createService();
        reference = createLocalReference(parent, TARGET_NAME);
    }

    private void createLocalReferenceToSiblingCompositeServiceConfiguration() throws Exception {
        final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
        //final InboundWire wire = createLocalInboundWire(sibling);
        sibling.getInboundWire(TARGET_SERVICE);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return localServiceInboundWire;
            }
        });
        EasyMock.replay(sibling);

        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return sibling;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createLocalService(topComposite);
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private void createLocalReferenceToSiblingCompositeServiceConfigurationNoMatchingBinding() throws Exception {
        final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
        sibling.getInboundWire(TARGET_SERVICE);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return null;
            }
        });
        EasyMock.replay(sibling);

        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return sibling;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createService();
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private void createLocalReferenceToSiblingAtomicConfiguration() throws Exception {
        final CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return atomicComponent;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite).atLeastOnce();
        EasyMock.replay(parent);
        atomicComponent = createAtomicTarget();
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private void createLocalReferenceToInvalidTarget() throws Exception {
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        Reference reference = EasyMock.createNiceMock(Reference.class);
        reference.getReferenceBindings();
        EasyMock.expectLastCall().andReturn(Collections.emptyList());
        EasyMock.replay(reference);
        EasyMock.expect(topComposite.getChild(TARGET)).andReturn(reference);
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);
        this.reference = createLocalReference(parent, TARGET_NAME);
    }

    private Reference createLocalReference(CompositeComponent parent, QualifiedName target) throws Exception {
        referenceBinding = createLocalReferenceBinding(target);
        Reference reference = new ReferenceImpl("foo", parent, contract);
        reference.addReferenceBinding(referenceBinding);
        return reference;
    }


}
