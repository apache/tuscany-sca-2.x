package org.apache.tuscany.core.system.component;

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
        SystemCompositeComponent<?> parent = new SystemCompositeComponentImpl("parent", null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        Source originalSource = new SourceImpl();
        Mock mock = mock(SystemAtomicComponent.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.stubs().method("getService").will(returnValue(originalSource));
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock.proxy();
        parent.register(context);

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
        SystemCompositeComponent<?> parent = new SystemCompositeComponentImpl("parent", null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);

        Source serviceSource = new SourceImpl();
        Mock mock = mock(SystemService.class);
        mock.stubs().method("getName").will(returnValue("service"));
        mock.stubs().method("getService").will(returnValue(serviceSource));
        mock.stubs().method("getInterface").will(returnValue(Source.class));
        SystemService serviceContext = (SystemService) mock.proxy();
        parent.register(serviceContext);

        Source atomicSource = new SourceImpl();
        Mock mock2 = mock(SystemAtomicComponent.class);
        mock2.stubs().method("getName").will(returnValue("source"));
        mock2.stubs().method("getService").will(returnValue(atomicSource));
        mock2.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock2.proxy();
        parent.register(context);

        Source source = parent.resolveExternalInstance(Source.class);
        assertSame(serviceSource, source);
        Source2 source2 = parent.resolveExternalInstance(Source2.class);
        assertNull(source2);
    }

    /**
     * Tests autowiring to a reference
     */
    public void testReferenceAutowire() throws Exception {
        SystemCompositeComponent<?> parent = new SystemCompositeComponentImpl("parent", null, null);
        parent.start();

        Source refSource = new SourceImpl();
        Mock mock = mock(SystemReference.class);
        mock.stubs().method("getName").will(returnValue("service"));
        mock.stubs().method("getService").will(returnValue(refSource));
        mock.stubs().method("getInterface").will(returnValue(Source.class));
        SystemReference referenceContext = (SystemReference) mock.proxy();
        parent.register(referenceContext);

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
