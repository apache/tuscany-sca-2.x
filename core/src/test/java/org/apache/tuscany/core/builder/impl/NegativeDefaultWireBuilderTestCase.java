package org.apache.tuscany.core.builder.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.mock.MockJavaOperationType;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.PojoMessageFactory;
import org.apache.tuscany.model.types.OperationType;

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
        MessageFactory msgFactory = new PojoMessageFactory();
        OperationType operation = new MockJavaOperationType(hello);

        InvocationConfiguration source = new InvocationConfiguration(operation);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<OperationType, InvocationConfiguration> sourceInvocationConfigs = new HashMap();
        sourceInvocationConfigs.put(operation, source);
        ProxyConfiguration sourceConfig = new ProxyConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), null, msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        InvocationConfiguration target = new InvocationConfiguration(operation);

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<OperationType, InvocationConfiguration> targetInvocationConfigs = new HashMap();
        targetInvocationConfigs.put(operation, target);
        ProxyConfiguration targetConfig = new ProxyConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), null, msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        try {
            builder.wire(sourceFactory, targetFactory, null, true, null);
            fail("Expected " + BuilderConfigException.class.getName());
        } catch (BuilderConfigException e) {
            // success
        }
    }

}
