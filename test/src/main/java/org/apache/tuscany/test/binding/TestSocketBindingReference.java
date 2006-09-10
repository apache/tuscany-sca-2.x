package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class TestSocketBindingReference<T> extends ReferenceExtension {

    private String host;
    private int port;

    public TestSocketBindingReference(String name,
                                      String host,
                                      int port,
                                      Class<T> interfaze,
                                      CompositeComponent parent,
                                      WireService wireService) {
        super(name, interfaze, parent, wireService);
        this.port = port;
        this.host = host;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestSocketInvoker(host, port, operation.getName());
    }
}
