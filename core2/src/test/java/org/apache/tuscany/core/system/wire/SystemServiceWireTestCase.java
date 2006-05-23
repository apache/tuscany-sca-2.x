package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.core.system.wire.SystemTargetWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceWireTestCase extends MockObjectTestCase {

    public void testWireResolution() throws NoSuchMethodException {
        Target originalTarget = new TargetImpl();
        // construct mock target
        Mock mock = mock(ComponentContext.class);
        mock.expects(once()).method("getService").will(returnValue(originalTarget));
        ComponentContext<Target> context = (ComponentContext<Target>) mock.proxy();
        TargetWire<Target> targetWire = new SystemTargetWire<Target>("Target",Target.class,context);

        // create service with wire
        SourceWire<Target> sourceWire = new SystemSourceWire<Target>("service", new QualifiedName("reference"), Target.class);    //String referenceName, QualifiedName targetName, Class<T> businessInterface
        sourceWire.setTargetWire(targetWire);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", sourceWire, null);
        Target target = serviceContext.getService();
        assertSame(target,originalTarget);
    }
}
