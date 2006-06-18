package org.apache.tuscany.container.java;

import java.util.Collections;

import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GetServiceByNameTestCase extends MockObjectTestCase {

    public void testServiceLocate() throws Exception {
        Mock mockScope = mock(ScopeContainer.class);
        mockScope.expects(atLeastOnce()).method("register");
        mockScope.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        ScopeContainer scope = (ScopeContainer) mockScope.proxy();

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
