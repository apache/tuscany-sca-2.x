package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemServiceBindingTestCase extends TestCase {

    public void testScope() throws Exception {
        SystemServiceBinding binding = new SystemServiceBinding(null, null, null);
        assertEquals(Scope.SYSTEM, binding.getScope());
    }

    public void testPrepare() throws Exception {
        SystemServiceBinding binding = new SystemServiceBinding(null, null, null);
        binding.prepare();
    }

    public void testIsSystemNoParent() throws Exception {
        SystemServiceBinding binding = new SystemServiceBinding(null, null, null);
        assertFalse(binding.isSystem());
    }

    public void testIsSystem() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.isSystem()).andReturn(true);
        EasyMock.replay(service);
        SystemServiceBinding binding = new SystemServiceBinding(null, null, null);
        binding.setService(service);
        assertTrue(binding.isSystem());
    }

    public void testIsNotSystem() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.isSystem()).andReturn(false);
        EasyMock.replay(service);
        SystemServiceBinding binding = new SystemServiceBinding(null, null, null);
        binding.setService(service);
        assertFalse(binding.isSystem());
    }

}
