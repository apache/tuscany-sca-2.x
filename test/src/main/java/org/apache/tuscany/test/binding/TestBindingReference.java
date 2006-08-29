package org.apache.tuscany.test.binding;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class TestBindingReference<T> extends ReferenceExtension<T> {

    public TestBindingReference(String name, Class<T> interfaze, CompositeComponent parent, WireService wireService) {
        super(name, interfaze, parent, wireService);
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        return new TestInvoker();
    }
}
