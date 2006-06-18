package org.apache.tuscany.container.java;

import java.lang.reflect.Constructor;

import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.component.PojoConfiguration;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * Validates wiring from a Java atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaReferenceWireTestCase extends MockObjectTestCase {

    public void testReferenceSet() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        final Target target = new TargetImpl();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addMember("target", SourceImpl.class.getMethod("setTarget", Target.class));
        configuration.addServiceInterface(Source.class);
        Constructor<SourceImpl> ctr = SourceImpl.class.getConstructor();
        configuration.setObjectFactory(new PojoObjectFactory<SourceImpl>(ctr));
        configuration.setScopeContainer(scope);
        Mock mock = mock(OutboundWire.class);
        mock.expects(atLeastOnce()).method("getInvocationChains");
        mock.expects(atLeastOnce()).method("getReferenceName").will(returnValue("target"));
        OutboundWire<Target> wire = (OutboundWire<Target>) mock.proxy();

        Mock mockService = mock(WireService.class);
        mockService.expects(atLeastOnce()).method("createProxy").with(eq(wire)).will(new Stub() {
            public Object invoke(Invocation invocation) throws Throwable {
                OutboundWire wire = (OutboundWire) invocation.parameterValues.get(0);
                wire.getInvocationChains();
                return target;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        configuration.setWireService((WireService) mockService.proxy());
        JavaAtomicComponent sourceContext = new JavaAtomicComponent("source", configuration);
        sourceContext.addOutboundWire(wire);
        sourceContext.start();
        Source source = (Source) sourceContext.getServiceInstance();
        assertSame(target, source.getTarget());
        scope.stop();
    }
}
