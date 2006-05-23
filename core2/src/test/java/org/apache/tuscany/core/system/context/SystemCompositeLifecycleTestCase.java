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
public class SystemCompositeLifecycleTestCase extends MockObjectTestCase {

    public void testLifecycle() throws Exception {
        SystemCompositeContext composite = new SystemCompositeContextImpl("foo", null, null);
        composite.start();
        assertNull(composite.getContext("nothtere"));
        composite.stop();
        composite.start();
        assertNull(composite.getContext("nothtere"));
        composite.stop();
    }

    public void testRestart() throws NoSuchMethodException {
        SystemCompositeContext composite = new SystemCompositeContextImpl("foo", null, null);
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicContext.class);
        mock.expects(atLeastOnce()).method("start");
        mock.expects(atLeastOnce()).method("stop");
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getService").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context = (SystemAtomicContext) mock.proxy();
        composite.registerContext(context);

        AtomicContext ctx = (AtomicContext) composite.getContext("source");
        Source source = (Source) ctx.getService();
        assertNotNull(source);
        composite.stop();
        composite.start();
        ctx = (AtomicContext) composite.getContext("source");
        Source source2 = (Source) ctx.getService();
        assertNotNull(source2);
        composite.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
