package org.apache.tuscany.container.java;

import java.util.Collections;

import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.component.PojoConfiguration;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GetServiceByNameTestCase extends MockObjectTestCase {

    public void testServiceLocate() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setObjectFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        configuration.setWireService(ArtifactFactory.createWireService());
        final JavaAtomicComponent<?> component = new JavaAtomicComponent("target", configuration);

        Mock mock = mock(InboundWire.class);
        mock.stubs().method("getBusinessInterface").will(returnValue(Target.class));
        mock.stubs().method("getServiceName").will(returnValue("Target"));
        mock.expects(atLeastOnce()).method("getInvocationChains").will(returnValue(Collections.emptyMap()));

        InboundWire<Target> wire = (InboundWire<Target>) mock.proxy();
        component.addInboundWire(wire);
        component.prepare();
        component.start();
        assertTrue(component.getServiceInstance("Target") instanceof Target);
    }


}
