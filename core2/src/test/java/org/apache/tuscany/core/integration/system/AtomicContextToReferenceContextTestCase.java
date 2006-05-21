package org.apache.tuscany.core.integration.system;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.MockContextFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.context.MockReferenceContext;
import org.apache.tuscany.core.mock.context.MockTargetWire;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * Tests wireing from an atomic context to a reference context
 *
 * @version $$Rev$$ $$Date$$
 */
public class AtomicContextToReferenceContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext context = new SystemCompositeContextImpl();
        scope.start();
        MockReferenceContext<Target> serviceContext = new MockReferenceContext<Target>("service", Target.class);
        TargetWire<Target> targetWire = new MockTargetWire<Target>(Target.class, new TargetImpl());
        serviceContext.setTargetWire(targetWire);
        context.registerContext(serviceContext);

        Map<String, Member> members = new HashMap<String, Member>();
        members.put("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicContext sourceContext = MockContextFactory.createSystemAtomicContext("source", interfaces, SourceImpl.class, null, members);
        SourceWire<Target> sourceWire = new SystemSourceWire<Target>("setTarget", new QualifiedName("service"), Target.class);
        sourceWire.setTargetWire(targetWire);
        sourceContext.addSourceWire(sourceWire);
        context.registerContext(sourceContext);
        sourceContext.setScopeContext(scope);
        context.start();
        MockReferenceContext serviceContext2 = (MockReferenceContext) context.getContext("service");
        assertSame(serviceContext, serviceContext2);
        Target target = (Target) serviceContext2.getService();
        assertSame(((Source) sourceContext.getService()).getTarget(), target);
    }
}
