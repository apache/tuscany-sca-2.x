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
public class TestBindingReference extends ReferenceExtension {

    public TestBindingReference(String name, Class<?> interfaze, CompositeComponent parent, WireService wireService) {
        super(name, interfaze, parent, wireService);
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestInvoker();
    }
}
