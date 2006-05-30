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
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
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
        Mock mockWire = mock(InboundWire.class);
        mockWire.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        InboundWire<Target> inboundWire = (InboundWire<Target>) mockWire.proxy();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicContext sourceContext = MockContextFactory.createSystemAtomicContext("source", scope, interfaces, SourceImpl.class, null, members);
        OutboundWire<Target> outboundWire = new SystemOutboundWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        outboundWire.setTargetWire(inboundWire);
        sourceContext.addOutboundWire(outboundWire);
        sourceContext.start();
        assertSame(((Source) sourceContext.getService()).getTarget(), target); // wires should pass back direct ref
    }
}
