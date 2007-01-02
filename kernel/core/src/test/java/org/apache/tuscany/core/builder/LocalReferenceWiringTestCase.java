package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

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
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }


    protected void setUp() throws Exception {
        super.setUp();

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

}
