package org.apache.tuscany.core.integration.system;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.TargetInstanceResolver;
import org.apache.tuscany.core.mock.MockContextFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.context.MockReferenceContext;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AtomicContextToReferenceContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException{
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext context = new SystemCompositeContextImpl();
        scope.start();
        ReferenceContext<Target> serviceContext = new MockReferenceContext<Target>("service", Target.class, new TargetImpl());
        context.registerContext(serviceContext);
        List<Injector> injectors = new ArrayList<Injector>();
        MethodInjector injector = new MethodInjector(SourceImpl.class.getMethod("setTarget", Target.class), new TargetInstanceResolver<Target>(serviceContext));
        injectors.add(injector);
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicContext sourceContext = MockContextFactory.createSystemAtomicContext("source", interfaces, SourceImpl.class, injectors);
        context.registerContext(sourceContext);
        sourceContext.setScopeContext(scope);
        context.start();
        MockReferenceContext serviceContext2 = (MockReferenceContext) context.getContext("service");
        assertSame(serviceContext, serviceContext2);
        Target target = (Target) serviceContext2.getService();
        assertSame(((Source) sourceContext.getService()).getTarget(), target);
    }
}
