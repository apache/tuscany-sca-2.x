package org.apache.tuscany.container.java.integration.context;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.MockReferenceContext;
import org.apache.tuscany.container.java.mock.components.SimpleSource;
import org.apache.tuscany.container.java.mock.components.SimpleSourceImpl;
import org.apache.tuscany.container.java.mock.components.SimpleTarget;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionStart;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.scope.HttpSessionScopeContext;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.core.context.scope.StatelessScopeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * Validates wiring from a Java atomic contexts by scope to a reference context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaToReferenceTestCase extends TestCase {

    public void testFromStatelessScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContext scope = new StatelessScopeContext(ctx);
        SystemCompositeContext parent = new SystemCompositeContextImpl();
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        SimpleSource source = (SimpleSource) parent.getContext("source").getService();
        assertEquals("hello", source.invokeHello());
        SimpleTarget target = (SimpleTarget) parent.getContext("target").getService();
        assertEquals("hello", target.echo("hello"));
        parent.stop();
        scope.stop();
    }

    public void testFromRequestScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final RequestScopeContext scope = new RequestScopeContext(ctx);
        final SystemCompositeContext parent = new SystemCompositeContextImpl();
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        scope.onEvent(new RequestStart(this));
        SimpleSource source = (SimpleSource) parent.getContext("source").getService();
        assertEquals("hello", source.invokeHello());
        SimpleTarget target = (SimpleTarget) parent.getContext("target").getService();
        assertEquals("hello", target.echo("hello"));
        scope.onEvent(new RequestEnd(this));
        parent.stop();
        scope.stop();
    }

    public void testFromSessionScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        SystemCompositeContext parent = new SystemCompositeContextImpl();
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        scope.onEvent(new HttpSessionStart(this, session1));
        SimpleSource source = (SimpleSource) parent.getContext("source").getService();
        assertEquals("hello", source.invokeHello());
        SimpleTarget target = (SimpleTarget) parent.getContext("target").getService();
        assertEquals("hello", target.echo("hello"));
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        scope.onEvent(new HttpSessionEnd(this, session1));
        parent.stop();
        scope.stop();
    }

    public void testFromModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext parent = new SystemCompositeContextImpl();
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        SimpleSource source = (SimpleSource) parent.getContext("source").getService();
        assertEquals("hello", source.invokeHello());
        SimpleTarget target = (SimpleTarget) parent.getContext("target").getService();
        assertEquals("hello", target.echo("hello"));
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();
    }

    private void setupComposite(CompositeContext<?> parent, ScopeContext scope) throws NoSuchMethodException {
        Connector connector = new ConnectorImpl();

        Map<String, Member> members = new HashMap<String, Member>();
        members.put("target", SimpleSourceImpl.class.getMethod("setTarget", SimpleTarget.class));
        JavaAtomicContext<?> sourceContext = MockContextFactory.createJavaAtomicContext("source", SimpleSourceImpl.class, SimpleSource.class,
                scope.getScope(), false, null, null, null, members);
        SourceWire<SimpleTarget> sourceWire = MockContextFactory.createSourceWire("target", SimpleTarget.class);
        sourceWire.setTargetName(new QualifiedName("target/Target"));
        sourceContext.addSourceWire(sourceWire);
        sourceContext.setScopeContext(scope);
        TargetWire targetWire = MockContextFactory.createTargetWire("Target", SimpleTarget.class);
        MockReferenceContext referenceContext = new MockReferenceContext("target", targetWire);
        parent.registerContext(sourceContext);
        parent.registerContext(referenceContext);

        connector.connect(sourceContext, parent);
        referenceContext.prepare();
        sourceContext.prepare();
    }

}
