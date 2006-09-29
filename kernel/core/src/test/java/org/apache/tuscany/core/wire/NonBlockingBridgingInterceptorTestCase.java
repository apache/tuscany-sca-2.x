package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class NonBlockingBridgingInterceptorTestCase extends TestCase {

    public void testInvoke() throws Exception {
        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        Message msg = new MessageImpl();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(next);
        Interceptor interceptor = new NonBlockingBridgingInterceptor(scheduler, context, next);
        interceptor.invoke(msg);
        verify(next);
    }

}
