package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests registering arbirarily deep child composite contexts
 *
 * @version $Rev$ $Date$
 */
public class CompositePropagationTestCase extends MockObjectTestCase {

    private SystemCompositeContext parent;
    private SystemCompositeContext child1;
    private SystemCompositeContext child2;

    public void testLifecyclePropagation() throws NoSuchMethodException {
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Mock mock = mock(SystemAtomicContext.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.expects(once()).method("stop");
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicContext context = (SystemAtomicContext) mock.proxy();
        child2.registerContext(context);
        parent.stop();
    }


    protected void setUp() throws Exception {
        super.setUp();
        parent = new SystemCompositeContextImpl("parent", null, null);
        child1 = new SystemCompositeContextImpl("child1", parent, null);
        child2 = new SystemCompositeContextImpl("child2", child1, null);
        child1.registerContext(child2);
        parent.registerContext(child1);
    }

    protected void tearDown() throws Exception {
        parent.stop();
        super.tearDown();
    }

}
