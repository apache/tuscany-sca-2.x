package org.apache.tuscany.container.java;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Validates wiring from a Java atomic contexts by scope to a reference context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaReferenceWireTestCase extends MockObjectTestCase {

    public void testReferenceSet() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Target target = new TargetImpl();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("target", SourceImpl.class.getMethod("setTarget", Target.class));
        JavaAtomicContext<?> sourceContext = MockContextFactory.createJavaAtomicContext("source", null, scope,
                SourceImpl.class, Source.class,
                scope.getScope(), false, null, null, null, members);

        Mock mock = mock(OutboundWire.class);
        mock.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        mock.expects(atLeastOnce()).method("getReferenceName").will(returnValue("target"));
        OutboundWire<Target> wire = (OutboundWire<Target>) mock.proxy();
        sourceContext.addReferenceWire(wire);

        sourceContext.start();

        Source source = (Source) sourceContext.getService();
        assertSame(target, source.getTarget());
        scope.stop();
    }
}
