package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class TestReferenceBinding extends ReferenceBindingExtension {

    public TestReferenceBinding(String name, CompositeComponent parent) {
        super(name, parent);
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestInvoker();
    }
}
