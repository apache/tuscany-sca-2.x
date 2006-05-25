package org.apache.tuscany.container.java;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInjectionTestCase extends TestCase {

    private Map<String, Member> members;

    public void testProxiedReferenceInjection() throws Exception {
        ScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class, scope,
                members, "target", Target.class, TargetImpl.class, scope);
        AtomicContext sourceContext = contexts.get("source");
        Source source = (Source) sourceContext.getService();
        Target target = source.getTarget();
        assertTrue(Proxy.isProxyClass(target.getClass()));

        assertNotNull(target);
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        members = new HashMap<String, Member>();
        Method m = SourceImpl.class.getMethod("setTarget", Target.class);
        members.put("target", m);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
