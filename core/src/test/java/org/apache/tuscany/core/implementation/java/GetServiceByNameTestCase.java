package org.apache.tuscany.core.implementation.java;

import java.util.Collections;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev: 415162 $$ $$Date: 2006-06-18 11:19:43 -0700 (Sun, 18 Jun 2006) $$
 */
public class GetServiceByNameTestCase extends MockObjectTestCase {

    public void testServiceLocate() throws Exception {
        Mock mockScope = mock(ScopeContainer.class);
        mockScope.expects(atLeastOnce()).method("register");
        mockScope.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        ScopeContainer scope = (ScopeContainer) mockScope.proxy();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        configuration.setWireService(new JDKWireService());
        final JavaAtomicComponent<?> component = new JavaAtomicComponent("target", configuration, null, null);

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
