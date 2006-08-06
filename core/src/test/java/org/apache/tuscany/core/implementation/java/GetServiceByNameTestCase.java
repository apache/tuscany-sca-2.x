package org.apache.tuscany.core.implementation.java;

import java.util.Collections;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * @version $$Rev: 415162 $$ $$Date: 2006-06-18 11:19:43 -0700 (Sun, 18 Jun 2006) $$
 */
public class GetServiceByNameTestCase extends TestCase {

    public void testServiceLocate() throws Exception {
        ScopeContainer scope = createMock(ScopeContainer.class);
        scope.register(EasyMock.isA(JavaAtomicComponent.class));
        expect(scope.getScope()).andReturn(Scope.MODULE);
        replay(scope);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        configuration.setWireService(new JDKWireService());
        final JavaAtomicComponent<?> component = new JavaAtomicComponent("target", configuration, null);

        InboundWire wire = createMock(InboundWire.class);
        expect(wire.getBusinessInterface()).andReturn(Target.class);
        expect(wire.getServiceName()).andReturn("Target");
        expect(wire.getInvocationChains()).andReturn(Collections.emptyMap());
        expect(wire.getCallbackReferenceName()).andReturn(null);
        replay(wire);
        component.addInboundWire(wire);
        component.prepare();
        component.start();
        assertTrue(component.getServiceInstance("Target") instanceof Target);
    }
}
