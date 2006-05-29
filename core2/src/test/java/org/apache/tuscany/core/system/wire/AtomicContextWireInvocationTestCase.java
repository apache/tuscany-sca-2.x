package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.wire.SystemReferenceWire;
import org.apache.tuscany.spi.wire.ServiceWire;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.QualifiedName;

/**
 * Tests wiring from an system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class AtomicContextWireInvocationTestCase extends MockObjectTestCase {

    public void testWireResolution() throws NoSuchMethodException {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Target target = new TargetImpl();
        Mock mockWire = mock(ServiceWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        ServiceWire<Target> serviceWire = (ServiceWire<Target>) mockWire.proxy();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicContext sourceContext = MockContextFactory.createSystemAtomicContext("source", scope, interfaces, SourceImpl.class, null, members);
        ReferenceWire<Target> referenceWire = new SystemReferenceWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        referenceWire.setTargetWire(serviceWire);
        sourceContext.addReferenceWire(referenceWire);
        sourceContext.start();
        assertSame(((Source) sourceContext.getService()).getTarget(), target); // wires should pass back direct ref
    }
}
