package org.apache.tuscany.core.system.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.DuplicateNameException;

import org.apache.tuscany.core.mock.component.Source;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verfies children with the same name cannot be registered in the same composite
 *
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends MockObjectTestCase {

    public void testDuplicateRegistration() throws Exception {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Mock mock = mock(SystemAtomicComponent.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.expects(once()).method("stop");
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context1 = (SystemAtomicComponent) mock.proxy();
        SystemAtomicComponent context2 = (SystemAtomicComponent) mock.proxy();
        parent.register(context1);
        try {
            parent.register(context2);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
