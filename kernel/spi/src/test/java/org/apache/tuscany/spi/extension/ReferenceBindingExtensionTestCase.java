package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceBindingExtensionTestCase extends TestCase {

    public void testScope() throws Exception {
        ReferenceBindingExtension binding = new MockBindingExtension();
        assertEquals(Scope.SYSTEM, binding.getScope());
    }

    public void testPrepare() throws Exception {
        ReferenceBindingExtension binding = new MockBindingExtension();
        binding.prepare();
    }

    public void testIsSystemNoParent() throws Exception {
        ReferenceBindingExtension binding = new MockBindingExtension();
        assertFalse(binding.isSystem());
    }

    public void testIsSystem() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.isSystem()).andReturn(true);
        EasyMock.replay(reference);
        ReferenceBindingExtension binding = new MockBindingExtension();
        binding.setReference(reference);
        assertTrue(binding.isSystem());
    }

    public void testIsNotSystem() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.isSystem()).andReturn(false);
        EasyMock.replay(reference);
        ReferenceBindingExtension binding = new MockBindingExtension();
        binding.setReference(reference);
        assertFalse(binding.isSystem());
    }

    private static class MockBindingExtension extends ReferenceBindingExtension {
        public MockBindingExtension() {
            super(null, null);
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
