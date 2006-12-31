package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemReferenceBindingTestCase extends TestCase {

    public void testScope() throws Exception {
        SystemReferenceBinding binding = new SystemReferenceBinding(null, null);
        assertEquals(Scope.SYSTEM, binding.getScope());
    }

    public void testPrepare() throws Exception {
        SystemReferenceBinding binding = new SystemReferenceBinding(null, null);
        binding.prepare();
    }

    public void testIsSystemNoParent() throws Exception {
        SystemReferenceBinding binding = new SystemReferenceBinding(null, null);
        assertFalse(binding.isSystem());
    }

    public void testIsSystem() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.isSystem()).andReturn(true);
        EasyMock.replay(reference);
        SystemReferenceBinding binding = new SystemReferenceBinding(null, null);
        binding.setReference(reference);
        assertTrue(binding.isSystem());
    }

    public void testIsNotSystem() throws Exception {
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.isSystem()).andReturn(false);
        EasyMock.replay(reference);
        SystemReferenceBinding binding = new SystemReferenceBinding(null, null);
        binding.setReference(reference);
        assertFalse(binding.isSystem());
    }

}
