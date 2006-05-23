package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.mock.component.Source;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends MockObjectTestCase {

    public void testDuplicateRegistration() throws Exception {
        SystemCompositeContext parent = new SystemCompositeContextImpl(null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Mock mock = mock(SystemAtomicContext.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.expects(once()).method("stop");
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context1 = (SystemAtomicContext) mock.proxy();
        SystemAtomicContext context2 = (SystemAtomicContext) mock.proxy();
        parent.registerContext(context1);
        try {
            parent.registerContext(context2);
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
