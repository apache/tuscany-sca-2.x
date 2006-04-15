package org.apache.tuscany.core.builder.impl;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.WireConfiguration;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.mock.MockHandler;
import org.apache.tuscany.core.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.invocation.mock.MockStaticInvoker;
import org.apache.tuscany.core.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

import java.lang.reflect.Method;
import java.util.Map;

public class DefaultWireBuilderTestCase extends TestCase {

    private Method hello;

    public DefaultWireBuilderTestCase() {
        super();
    }

    public DefaultWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testWireWithInterceptorsAndHandlers() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithSourceInterceptorTargetHandlersAndTargetInterceptor() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithInterceptorsAndRequestHandlers() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockHandler sourceRequestHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithSourceAndTargetInterceptors() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithSourceInterceptorSourceHandlersAndTargetInterceptor() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithTargetInterceptorAndTargetHandlers() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        // source.build();
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testWireWithTargetInterceptor() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * When there are only {@link InvokerInterceptor}s in the source and target chain, we need to bypass one during
     * wire up so they are not chained together
     */
    public void testWireWithOnlyInvokerInterceptors() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        source.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        // no need for scopes since we use a static invoker
        builder.connect(sourceFactory, targetFactory, null, true, null);
        target.build();
        // set a static invoker
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
    }

}
