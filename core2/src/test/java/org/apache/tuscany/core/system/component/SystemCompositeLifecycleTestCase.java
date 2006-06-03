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
public class SystemCompositeLifecycleTestCase extends MockObjectTestCase {

    public void testLifecycle() throws Exception {
        SystemCompositeComponent composite = new SystemCompositeComponentImpl("foo", null, null);
        composite.start();
        assertNull(composite.getChild("nothtere"));
        composite.stop();
        composite.start();
        assertNull(composite.getChild("nothtere"));
        composite.stop();
    }

    public void testRestart() throws NoSuchMethodException {
        SystemCompositeComponent composite = new SystemCompositeComponentImpl("foo", null, null);
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicComponent.class);
        mock.expects(atLeastOnce()).method("start");
        mock.expects(atLeastOnce()).method("stop");
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getServiceInstance").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock.proxy();
        composite.register(context);

        AtomicComponent ctx = (AtomicComponent) composite.getChild("source");
        Source source = (Source) ctx.getServiceInstance();
        assertNotNull(source);
        composite.stop();
        composite.start();
        ctx = (AtomicComponent) composite.getChild("source");
        Source source2 = (Source) ctx.getServiceInstance();
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
