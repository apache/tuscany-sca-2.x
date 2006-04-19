package org.apache.tuscany.core.builder.impl;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.wire.mock.SimpleTarget;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

import java.lang.reflect.Method;
import java.util.Map;

public class NegativeDefaultWireBuilderTestCase extends TestCase {

    private Method hello;

    private ProxyFactoryFactory factory = new JDKProxyFactoryFactory();

    public NegativeDefaultWireBuilderTestCase() {
        super();
    }

    public NegativeDefaultWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testNoTargetInterceptorOrHandler() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);

        SourceWireFactory sourceFactory = new JDKProxyFactoryFactory().createSourceWireFactory();
        Map<Method, SourceInvocationConfiguration> sourceInvocationConfigs = new MethodHashMap<SourceInvocationConfiguration>();
        sourceInvocationConfigs.put(hello, source);
        WireSourceConfiguration sourceConfig = new WireSourceConfiguration("foo",new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);

        TargetWireFactory targetFactory = factory.createTargetWireFactory();
        Map<Method, TargetInvocationConfiguration> targetInvocationConfigs = new MethodHashMap<TargetInvocationConfiguration>();
        targetInvocationConfigs.put(hello, target);
        WireTargetConfiguration targetConfig = new WireTargetConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setConfiguration(targetConfig);
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
