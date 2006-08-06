package org.apache.tuscany.container.groovy;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import groovy.lang.GroovyObject;
import junit.framework.TestCase;
import org.apache.tuscany.container.groovy.mock.AsyncTarget;
import org.apache.tuscany.core.policy.async.AsyncMonitor;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AsyncInvokerTestCase extends TestCase {

    public void testInvoke() throws Exception {
        GroovyObject instance = createMock(GroovyObject.class);
        expect(instance.invokeMethod("invoke", null)).andReturn(null).once();
        replay(instance);
        GroovyAtomicComponent component = EasyMock.createMock(GroovyAtomicComponent.class);
        expect(component.getTargetInstance()).andReturn(instance);
        EasyMock.replay(component);
        AsyncMonitor monitor = createMock(AsyncMonitor.class);
        replay(monitor);

        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        Method method = AsyncTarget.class.getMethod("invoke");
        method.setAccessible(true);
        AsyncGroovyInvoker invoker = new AsyncGroovyInvoker("invoke", null, component, scheduler, monitor, context);
        Message msg = new MessageImpl();
        invoker.invoke(msg);
        verify(instance);
    }

}
