package org.apache.tuscany.core.builder.impl;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

public class NegativeDefaultWireBuilderTestCase extends TestCase {

    private Method hello;

    private Method goodbye;

    public NegativeDefaultWireBuilderTestCase() {
        super();
    }

    public NegativeDefaultWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", new Class[] { String.class });
        goodbye = SimpleTarget.class.getMethod("goodbye", new Class[] { String.class });
    }

    public void testNoTargetInterceptorOrHandler() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        ProxyConfiguration sourceConfig = new ProxyConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(hello);

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        ProxyConfiguration targetConfig = new ProxyConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        try {
            builder.connect(sourceFactory, targetFactory, null, true, null);
            fail("Expected " + BuilderConfigException.class.getName());
        } catch (BuilderConfigException e) {
            // success
        }
    }

}
