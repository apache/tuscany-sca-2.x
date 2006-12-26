package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class TestSocketBindingReference extends ReferenceExtension {

    private String host;
    private int port;

    public TestSocketBindingReference(String name,
                                      String host,
                                      int port,
                                      CompositeComponent parent) {
        super(name, parent);
        this.port = port;
        this.host = host;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestSocketInvoker(host, port, operation.getName());
    }
}
