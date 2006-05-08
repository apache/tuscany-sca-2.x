package org.apache.tuscany.container.java.context;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInjectionTestCase extends TestCase {

    private Map<String, Member> members;

    public void testProxiedReferenceInjection() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext<AtomicContext> scope = new ModuleScopeContext(ctx);
        scope.start();
        JavaAtomicContext targetContext = MockFactory.createJavaAtomicContext("source", TargetImpl.class);
        TargetWireFactory targetWireFactory = MockFactory.createTargetWireFactory("Target",Target.class);
        targetContext.addTargetWireFactory(targetWireFactory);
        JavaAtomicContext sourceContext = MockFactory.createJavaAtomicContext("source", SourceImpl.class, false, null, null, null, members);
        SourceWireFactory sourceWireFactory = MockFactory.createSourceWireFactory("target", new QualifiedName("target"), Target.class);
        sourceContext.addSourceWireFactory(sourceWireFactory);
        scope.register(targetContext);
        sourceContext.setScopeContext(scope);
        scope.register(sourceContext);
        targetContext.setScopeContext(scope);
        MockFactory.connect(sourceWireFactory,targetWireFactory,targetContext,false);
        Source source = (Source)sourceContext.getInstance(null);
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
