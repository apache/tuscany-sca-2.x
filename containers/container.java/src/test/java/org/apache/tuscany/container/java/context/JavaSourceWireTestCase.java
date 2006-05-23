package org.apache.tuscany.container.java.context;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Validates wiring from a Java atomic contexts by scope to a reference context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaSourceWireTestCase extends MockObjectTestCase {

    public void testReferenceSet() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Target target = new TargetImpl();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("target", SourceImpl.class.getMethod("setTarget", Target.class));
        JavaAtomicContext<?> sourceContext = MockContextFactory.createJavaAtomicContext("source", null,
                SourceImpl.class, Source.class,
                scope.getScope(), false, null, null, null, members);

        Mock mock = mock(SourceWire.class);
        mock.expects(atLeastOnce()).method("getTargetService").will(returnValue(target));
        mock.expects(atLeastOnce()).method("getReferenceName").will(returnValue("target"));
        SourceWire<Target> wire = (SourceWire<Target>) mock.proxy();
        sourceContext.addSourceWire(wire);

        sourceContext.setScopeContext(scope);
        Source source = (Source) sourceContext.getService();
        assertSame(target, source.getTarget());
        scope.stop();
    }
}
