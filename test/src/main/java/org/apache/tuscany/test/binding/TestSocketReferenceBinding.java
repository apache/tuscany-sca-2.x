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
public class TestSocketReferenceBinding extends ReferenceBindingExtension {
    private static final QName BINDING_TEST = new QName(Version.XML_NAMESPACE_1_0, "binding.socket");

    private String host;
    private int port;

    public TestSocketReferenceBinding(String name,
                                      String host,
                                      int port,
                                      CompositeComponent parent) {
        super(name, parent);
        this.port = port;
        this.host = host;
    }

    public QName getBindingType() {
        return BINDING_TEST;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new TestSocketInvoker(host, port, operation.getName());
    }
}
