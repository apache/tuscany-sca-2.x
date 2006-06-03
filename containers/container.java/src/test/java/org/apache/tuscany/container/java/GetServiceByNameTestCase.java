package org.apache.tuscany.container.java;

import java.util.Collections;

import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GetServiceByNameTestCase extends MockObjectTestCase {

    public void testServiceLocate() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        final JavaAtomicComponent<?> context =
                MockContextFactory.createJavaAtomicContext("target", scope, TargetImpl.class, Target.class, Scope.MODULE);

        Mock mock = mock(InboundWire.class);
        mock.stubs().method("getBusinessInterface").will(returnValue(Target.class));
        mock.stubs().method("getServiceName").will(returnValue("Target"));
        mock.expects(atLeastOnce()).method("getInvocationChains").will(returnValue(Collections.emptyMap()));

        InboundWire<Target> wire = (InboundWire<Target>) mock.proxy();
        context.addInboundWire(wire);
        context.prepare();
        context.start();
        assertTrue(context.getServiceInstance("Target") instanceof Target);
    }

    

}
