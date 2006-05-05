package org.apache.tuscany.core.wire.jdk;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InvocationConfigurationImpl;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.mock.SimpleTarget;
import org.apache.tuscany.core.wire.mock.SimpleTargetImpl;
import org.apache.tuscany.core.wire.mock.MockStaticInvoker;
import org.apache.tuscany.core.wire.mock.MockHandler;
import org.apache.tuscany.core.wire.mock.MockSyncInterceptor;
import org.apache.tuscany.core.wire.SourceInvocationConfigurationImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.TargetInvocationConfigurationImpl;
import org.apache.tuscany.spi.wire.SourceInvocationConfiguration;
import org.apache.tuscany.spi.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.InvocationConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

public class JDKInvocationHandlerTestCase extends TestCase {

    private Method hello;

    public JDKInvocationHandlerTestCase() {
        super();
    }

    public JDKInvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testBasicInvoke() throws Throwable {
        Map<Method, InvocationConfiguration> configs = new MethodHashMap<InvocationConfiguration>();
        configs.put(hello, getInvocationHandler(hello));
        JDKInvocationHandler handler = new JDKInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[] { "foo" }));
    }

    public void testErrorInvoke() throws Throwable {
        Map<Method, InvocationConfiguration> configs = new MethodHashMap<InvocationConfiguration>();
        configs.put(hello, getInvocationHandler(hello));
        JDKInvocationHandler handler = new JDKInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[] {}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        SourceInvocationConfigurationImpl source = new SourceInvocationConfigurationImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InvocationConfiguration> configs = new MethodHashMap<InvocationConfiguration>();
        configs.put(hello, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[] {}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        SourceInvocationConfigurationImpl source = new SourceInvocationConfigurationImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InvocationConfigurationImpl> configs = new MethodHashMap<InvocationConfigurationImpl>();
        configs.put(hello, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[] { "foo" }));
    }

    private InvocationConfiguration getInvocationHandler(Method m) {
        SourceInvocationConfiguration source = new SourceInvocationConfigurationImpl(m);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        TargetInvocationConfiguration target = new TargetInvocationConfigurationImpl(m);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();
        MockStaticInvoker invoker = new MockStaticInvoker(m, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);
        return source;
    }
}
