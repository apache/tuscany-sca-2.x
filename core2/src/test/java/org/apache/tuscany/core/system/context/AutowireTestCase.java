package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.MockContextFactory;
import org.apache.tuscany.core.mock.context.MockReferenceContext;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.core.system.wire.SystemTargetWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AutowireTestCase extends TestCase {

    public void testAtomicAutowire() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", interfaces, SourceImpl.class);
        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        parent.registerContext(context);
        scopeContext.publish(new ModuleStart(this, parent));
        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        Source2 source2 = parent.resolveInstance(Source2.class);
        assertSame(source, source2);
        assertNull(parent.resolveExternalInstance(Source.class));
    }

    public void testServiceAutowire() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", interfaces, SourceImpl.class);
        scopeContext.register(context);
        context.setScopeContext(scopeContext);

        TargetWire<Source> targetWire = new SystemTargetWire<Source>(Source.class, context);
        SourceWire<Source> wire = new SystemSourceWire<Source>("sourceService", new QualifiedName("source"), Source.class);
        wire.setTargetWire(targetWire);

        SystemServiceContext<Source> serviceContext = new SystemServiceContextImpl<Source>("sourceService", Source.class, wire, parent);
        parent.registerContext(serviceContext);
        parent.registerContext(context);
        scopeContext.publish(new ModuleStart(this, parent));
        Source source = parent.resolveExternalInstance(Source.class);
        assertNotNull(source);
        Source2 source2 = parent.resolveExternalInstance(Source2.class);
        assertNull(source2);
    }

    public void testReferenceAutowire() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext<?> parent = new SystemCompositeContextImpl("parent", null, null);
        parent.start();
        ReferenceContext<Source> referenceContext = new MockReferenceContext<Source>(
                "sourceReference", Source.class, new SourceImpl());
        parent.registerContext(referenceContext);
        scopeContext.publish(new ModuleStart(this, parent));
        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        assertNull(parent.resolveExternalInstance(Source.class));
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
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
