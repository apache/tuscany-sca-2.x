package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SynchronousBridgingInterceptorTestCase extends TestCase {

    public void testInvoke() throws Exception {
        Message msg = new MessageImpl();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(next);
        Interceptor interceptor = new SynchronousBridgingInterceptor(next);
        interceptor.invoke(msg);
        verify(next);
    }

}
