package org.apache.tuscany.core.system.wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.wire.SystemServiceWire;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * Tests a wire from a service context to an atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceContextToAtomicContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        SystemAtomicContext targetContext = MockContextFactory.createSystemAtomicContext("target", scope, TargetImpl.class);
        ServiceWire<Target> serviceWire = new SystemServiceWire<Target>(Target.class, targetContext);
//        ServiceWire<Target> wire = new SystemServiceWire<Target>(Target.class);    //String referenceName, QualifiedName targetName, Class<T> businessInterface
//        wire.setTargetWire(referenceWire);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", serviceWire, null);
        serviceContext.start();
        targetContext.start();
        
        Target target = serviceContext.getService();
        assertSame(targetContext.getService(), target);
    }
}
