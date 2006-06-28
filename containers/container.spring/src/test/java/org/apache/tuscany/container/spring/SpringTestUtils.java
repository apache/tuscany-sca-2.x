package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * @version $$Rev: $$ $$Date: $$
 */

public class SpringTestUtils {

    public static <T> Service<T> createService( String name,
                                                Class<T> serviceInterface,
                                                CompositeComponent parent,
                                                WireService wireService )
    {
        Service<T> service = new ServiceExtension<T>(name, parent, wireService);
        InboundWire<T> inboundWire = ArtifactFactory.createInboundWire(name, serviceInterface);
        OutboundWire<T> outboundWire = ArtifactFactory.createOutboundWire(name, serviceInterface);
        ArtifactFactory.terminateWire(outboundWire);
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        outboundWire.setTargetName(new QualifiedName("foo"));
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundWire, true);
        ArtifactFactory.terminateWire(inboundWire);
        return service;
    }
}
