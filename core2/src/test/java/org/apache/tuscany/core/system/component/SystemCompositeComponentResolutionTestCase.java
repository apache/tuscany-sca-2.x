package org.apache.tuscany.core.system.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemCompositeComponentResolutionTestCase extends MockObjectTestCase {

    public void testContextResolution() throws NoSuchMethodException {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("foo", null, null);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicComponent.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getService").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock.proxy();
        parent.register(context);
        AtomicComponent ctx = (AtomicComponent) parent.getChild("source");
        Source source = (Source) ctx.getService();
        assertNotNull(source);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
