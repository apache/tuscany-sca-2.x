package org.apache.tuscany.core.integration.system;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.context.MockReferenceContext;
import org.apache.tuscany.core.mock.context.MockTargetWire;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.context.SystemReferenceContext;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * Tests wiring from a service context directly to an atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceContextToReferenceContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext context = new SystemCompositeContextImpl();
        scope.start();
        SystemReferenceContext<Target> referenceContext = new MockReferenceContext<Target>("reference", Target.class);
        context.registerContext(referenceContext);
        TargetWire<Target> targetWire = new MockTargetWire<Target>(Target.class, new TargetImpl());
        SourceWire<Target> wire = new SystemSourceWire<Target>("service", new QualifiedName("reference"), Target.class);    //String referenceName, QualifiedName targetName, Class<T> businessInterface
        wire.setTargetWire(targetWire);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", wire, context);
        context.registerContext(serviceContext);
        context.start();
        SystemServiceContext serviceContext2 = (SystemServiceContext) context.getContext("service");
        assertSame(serviceContext, serviceContext2);
        Target target = (Target) serviceContext2.getService();
        assertNotNull(target);
    }
}
