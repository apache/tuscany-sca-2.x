package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests wiring from an system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class AtomicComponentWireInvocationTestCase extends MockObjectTestCase {

    public void testWireResolution() throws NoSuchMethodException {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Target target = new TargetImpl();
        Mock mockWire = mock(SystemInboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        SystemInboundWire<Target> inboundWire = (SystemInboundWire<Target>) mockWire.proxy();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicComponent sourceContext = MockContextFactory.createSystemAtomicContext("source", scope, interfaces, SourceImpl.class, null, members);
        OutboundWire<Target> outboundWire = new SystemOutboundWireImpl<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(inboundWire);
        sourceContext.addOutboundWire(outboundWire);
        sourceContext.start();
        assertSame(((Source) sourceContext.getService()).getTarget(), target); // wires should pass back direct ref
    }
}
