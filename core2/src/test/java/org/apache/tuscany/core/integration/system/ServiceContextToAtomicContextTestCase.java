package org.apache.tuscany.core.integration.system;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.MockContextFactory;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.core.system.wire.SystemTargetWire;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.QualifiedName;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ServiceContextToAtomicContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext context = new SystemCompositeContextImpl();
        scope.start();
        SystemAtomicContext targetContext = MockContextFactory.createSystemAtomicContext("target", TargetImpl.class);
        context.registerContext(targetContext);
        targetContext.setScopeContext(scope);
        TargetWire<Target> targetWire = new SystemTargetWire<Target>(Target.class,targetContext);
        SourceWire<Target> wire = new SystemSourceWire<Target>("service",new QualifiedName("target"),Target.class);    //String referenceName, QualifiedName targetName, Class<T> businessInterface
        wire.setTargetWire(targetWire);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", wire, context);
        context.registerContext(serviceContext);
        context.start();
        SystemServiceContext serviceContext2 = (SystemServiceContext) context.getContext("service");
        assertSame(serviceContext, serviceContext2);
        Target target = (Target) serviceContext2.getService();
        assertSame(targetContext.getService(), target);
    }
}
