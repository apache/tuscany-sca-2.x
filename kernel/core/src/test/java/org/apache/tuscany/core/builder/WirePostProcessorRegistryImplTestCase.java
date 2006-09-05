package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.WirePostProcessor;
import org.apache.tuscany.spi.builder.WirePostProcessorRegistry;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class WirePostProcessorRegistryImplTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testRegisterUnregister() throws Exception {
        WirePostProcessorRegistry registry = new WirePostProcessorRegistryImpl();
        OutboundWire owire = EasyMock.createMock(OutboundWire.class);
        InboundWire iwire = EasyMock.createMock(InboundWire.class);
        WirePostProcessor processor = createMock(WirePostProcessor.class);
        processor.process(EasyMock.eq(owire), EasyMock.eq(iwire));
        EasyMock.replay(processor);
        registry.register(processor);
        registry.process(owire, iwire);
        registry.unregister(processor);
        registry.process(owire, iwire);
        verify(processor);
    }

    @SuppressWarnings("unchecked")
    public void testProcessInboundToOutbound() throws Exception {
        WirePostProcessorRegistry registry = new WirePostProcessorRegistryImpl();
        OutboundWire owire = EasyMock.createMock(OutboundWire.class);
        InboundWire iwire = EasyMock.createMock(InboundWire.class);
        WirePostProcessor processor = createMock(WirePostProcessor.class);
        processor.process(EasyMock.eq(iwire), EasyMock.eq(owire));
        EasyMock.replay(processor);
        registry.register(processor);
        registry.process(iwire, owire);
        verify(processor);
    }


}
