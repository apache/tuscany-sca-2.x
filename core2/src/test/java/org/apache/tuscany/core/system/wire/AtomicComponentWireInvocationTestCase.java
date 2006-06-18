package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests wiring from an system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class AtomicComponentWireInvocationTestCase extends MockObjectTestCase {

    public void testWireResolution() throws NoSuchMethodException {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        Target target = new TargetImpl();
        Mock mockWire = mock(SystemInboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemInboundWire<Target> inboundWire = (SystemInboundWire<Target>) mockWire.proxy();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.addMember("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        configuration.addServiceInterface(Source.class);
        configuration.setObjectFactory(new PojoObjectFactory(SourceImpl.class.getConstructor()));
        SystemAtomicComponent sourceContext = new SystemAtomicComponentImpl("source", configuration);
        OutboundWire<Target> outboundWire =
            new SystemOutboundWireImpl<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(inboundWire);
        sourceContext.addOutboundWire(outboundWire);
        sourceContext.start();
        assertSame(((Source) sourceContext.getServiceInstance()).getTarget(),
            target); // wires should pass back direct ref
    }
}
