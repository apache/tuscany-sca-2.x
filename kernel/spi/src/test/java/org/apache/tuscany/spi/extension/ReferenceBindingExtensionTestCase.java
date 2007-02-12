package org.apache.tuscany.spi.extension;

import java.net.URI;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceBindingExtensionTestCase extends TestCase {

    public void testPrepare() throws Exception {
        ReferenceBindingExtension binding = new MockBindingExtension();
        binding.prepare();
    }

    private static class MockBindingExtension extends ReferenceBindingExtension {
        public MockBindingExtension() {
            super(URI.create("_foo"));
        }

        public QName getBindingType() {
            return null;
        }

        public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation)
            throws TargetInvokerCreationException {
            return null;
        }
    }
}
