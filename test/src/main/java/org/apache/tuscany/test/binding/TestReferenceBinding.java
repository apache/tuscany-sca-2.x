package org.apache.tuscany.test.binding;

import javax.xml.namespace.QName;

import org.osoa.sca.Version;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class TestReferenceBinding extends ReferenceBindingExtension {
    private static final QName BINDING_TEST = new QName(Version.XML_NAMESPACE_1_0, "binding.socket");

    public TestReferenceBinding(String name, CompositeComponent parent) {
        super(name, parent);
    }

    public QName getBindingType() {
        return BINDING_TEST;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestInvoker();
    }
}
