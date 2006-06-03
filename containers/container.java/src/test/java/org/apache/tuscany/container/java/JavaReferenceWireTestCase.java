package org.apache.tuscany.container.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
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
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        final Target target = new TargetImpl();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("target", SourceImpl.class.getMethod("setTarget", Target.class));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Constructor<SourceImpl> ctr = SourceImpl.class.getConstructor();

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
        WireService wireService = (WireService) mockService.proxy();
        JavaAtomicComponent sourceContext = new JavaAtomicComponent("source", null, scope, interfaces,
                new PojoObjectFactory<SourceImpl>(ctr), scope.getScope(), false, null, null, null, members, wireService);

        sourceContext.addOutboundWire(wire);
        sourceContext.start();
        Source source = (Source) sourceContext.getService();
        assertSame(target, source.getTarget());
        scope.stop();
    }
}
