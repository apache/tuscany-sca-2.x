package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.spi.context.AtomicContext;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemCompositeContextResolutionTestCase extends MockObjectTestCase {

    public void testContextResolution() throws NoSuchMethodException {
        SystemCompositeContext parent = new SystemCompositeContextImpl("foo", null, null);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicContext.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getService").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context = (SystemAtomicContext) mock.proxy();
        parent.registerContext(context);
        AtomicContext ctx = (AtomicContext) parent.getContext("source");
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
