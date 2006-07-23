package org.apache.tuscany.core.implementation.java.mock;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.services.work.WorkScheduler;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.AsyncJavaTargetInvoker;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import static org.apache.tuscany.core.implementation.java.mock.MockFactory.createJavaComponent;
import org.apache.tuscany.core.implementation.java.mock.components.AsyncTarget;
import static org.easymock.EasyMock.createMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaAtomicComponentAsyncTestCase extends TestCase {

    /**
     * Verifies a non-blocking invoker is created for an asynchronous operation
     */
    public void testAsyncTargetInvoker() throws Exception {
        WorkScheduler scheduler = createMock(WorkScheduler.class);
        ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        JavaAtomicComponent component = createJavaComponent("foo", scopeContainer, getClass(), scheduler);
        Method method = AsyncTarget.class.getMethod("invoke");
        assertTrue(component.createTargetInvoker("foo", method) instanceof AsyncJavaTargetInvoker);
    }
}
