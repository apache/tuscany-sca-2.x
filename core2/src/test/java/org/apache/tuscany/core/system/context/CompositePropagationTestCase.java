package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.Source;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests registering arbirarily deep child composite contexts
 *
 * @version $Rev$ $Date$
 */
public class CompositePropagationTestCase extends MockObjectTestCase {

    private SystemCompositeComponent parent;
    private SystemCompositeComponent child1;
    private SystemCompositeComponent child2;

    public void testLifecyclePropagation() throws NoSuchMethodException {
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Mock mock = mock(SystemAtomicComponent.class);
        mock.stubs().method("getName").will(returnValue("source"));
        mock.expects(once()).method("stop");
        mock.stubs().method("getServiceInterfaces").will(returnValue(interfaces));
        SystemAtomicComponent context = (SystemAtomicComponent) mock.proxy();
        child2.register(context);
        parent.stop();
    }


    protected void setUp() throws Exception {
        super.setUp();
        parent = new SystemCompositeComponentImpl("parent", null, null);
        child1 = new SystemCompositeComponentImpl("child1", parent, null);
        child2 = new SystemCompositeComponentImpl("child2", child1, null);
        child1.register(child2);
        parent.register(child1);
    }

    protected void tearDown() throws Exception {
        parent.stop();
        super.tearDown();
    }

}
