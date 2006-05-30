package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Performs basic autowiring tests to composite artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireTestCase extends MockObjectTestCase {

    /**
     * Tests autowiring to an atomic context
     *
     * @throws Exception
     */
    public void testAtomicAutowire() throws Exception {
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicContext.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getService").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context = (SystemAtomicContext) mock.proxy();
        parent.registerContext(context);

        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        Source2 source2 = parent.resolveInstance(Source2.class);
        assertSame(source, source2);
        assertNull(parent.resolveExternalInstance(Source.class));
    }

    /**
     * Tests autowiring to a service context which is wired to an atomic context.
     */
    public void testServiceAutowire() throws Exception {
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);

        Source serviceSource = new SourceImpl();
        Mock mock = mock(SystemServiceContext.class);
        mock.stubs().method("getName").will(returnValue("service"));
        mock.stubs().method("getService").will(returnValue(serviceSource));
        mock.stubs().method("getInterface").will(returnValue(Source.class));
        SystemServiceContext serviceContext = (SystemServiceContext) mock.proxy();
        parent.registerContext(serviceContext);

        Source atomicSource = new SourceImpl();
        Mock mock2 = mock(SystemAtomicContext.class);
        mock2.stubs().method("getName").will(returnValue("source"));
        mock2.stubs().method("getService").will(returnValue(atomicSource));
        mock2.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context = (SystemAtomicContext) mock2.proxy();
        parent.registerContext(context);

        Source source = parent.resolveExternalInstance(Source.class);
        assertSame(serviceSource, source);
        Source2 source2 = parent.resolveExternalInstance(Source2.class);
        assertNull(source2);
    }

    /**
     * Tests autowiring to a reference
     */
    public void testReferenceAutowire() throws Exception {
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();

        Source refSource = new SourceImpl();
        Mock mock = mock(SystemReferenceContext.class);
        mock.stubs().method("getName").will(returnValue("service"));
        mock.stubs().method("getService").will(returnValue(refSource));
        mock.stubs().method("getInterface").will(returnValue(Source.class));
        SystemReferenceContext referenceContext = (SystemReferenceContext) mock.proxy();
        parent.registerContext(referenceContext);

        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        assertNull(parent.resolveExternalInstance(Source.class));
    }

    public static class SourceImpl implements Source, Source2 {
        public SourceImpl() {
        }
    }

    public static interface Source {

    }

    public static interface Source2 {
    }

}
