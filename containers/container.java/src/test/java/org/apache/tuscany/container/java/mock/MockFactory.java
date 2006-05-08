package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.invocation.ScopedJavaComponentInvoker;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.SourceInvocationConfigurationImpl;
import org.apache.tuscany.core.wire.TargetInvocationConfigurationImpl;
import org.apache.tuscany.core.wire.WireSourceConfigurationImpl;
import org.apache.tuscany.core.wire.WireTargetConfigurationImpl;
import org.apache.tuscany.core.wire.jdk.JDKSourceWireFactory;
import org.apache.tuscany.core.wire.jdk.JDKTargetWireFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.wire.SourceInvocationConfiguration;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.spi.wire.WireFactoryInitException;
import org.apache.tuscany.spi.wire.WireSourceConfiguration;
import org.apache.tuscany.spi.wire.WireTargetConfiguration;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockFactory {

    public static JavaAtomicContext createJavaAtomicContext(String name, Class<?> clazz) throws NoSuchMethodException {
        return createJavaAtomicContext(name, clazz, false, null, null, null, null);

    }

    public static JavaAtomicContext createJavaAtomicContext(String name, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                            EventInvoker<Object> destroyInvoker, List<Injector> injectors, Map<String, Member> members) throws NoSuchMethodException {
        return new JavaAtomicContext(name, createObjectFactory(clazz, null), eagerInit, initInvoker, destroyInvoker, injectors, members);
    }

    public static TargetWireFactory createTargetWireFactory(String serviceName, Class<?> interfaze) throws WireFactoryInitException {
        WireTargetConfiguration wireConfiguration = new WireTargetConfigurationImpl(serviceName, createTargetInvocationConfigurations(interfaze));
        TargetWireFactory wireFactory = new JDKTargetWireFactory();
        wireFactory.setBusinessInterface(interfaze);
        wireFactory.setConfiguration(wireConfiguration);
        wireFactory.initialize();
        return wireFactory;
    }

    public static Map<Method, TargetInvocationConfiguration> createTargetInvocationConfigurations(Class<?> interfaze) {
        Map<Method, TargetInvocationConfiguration> invocations = new HashMap<Method, TargetInvocationConfiguration>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            TargetInvocationConfiguration iConfig = new TargetInvocationConfigurationImpl(method);
            // add tail interceptor
            iConfig.addInterceptor(new InvokerInterceptor());
            invocations.put(method, iConfig);
        }
        return invocations;
    }

    public static SourceWireFactory createSourceWireFactory(String refName, QualifiedName targetName, Class<?> interfaze) throws WireFactoryInitException {
        WireSourceConfiguration wireConfiguration = new WireSourceConfigurationImpl(refName, targetName, createSourceInvocationConfigurations(interfaze));
        SourceWireFactory wireFactory = new JDKSourceWireFactory();
        wireFactory.setBusinessInterface(interfaze);
        wireFactory.setConfiguration(wireConfiguration);
        wireFactory.initialize();
        return wireFactory;
    }

    public static void connect(SourceWireFactory<?> sourceFactory, TargetWireFactory<?> targetFactory, AtomicContext targetContext, boolean cacheable) throws Exception {
        if (targetFactory != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, TargetInvocationConfiguration> targetInvocationConfigs = targetFactory.getConfiguration().getInvocationConfigurations();
            for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration()
                    .getInvocationConfigurations().values()) {
                // match wire chains
                TargetInvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
                if (targetInvocationConfig == null) {
                    BuilderConfigException e = new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(sourceFactory.getConfiguration().getReferenceName());
                    throw e;
                }
                // if handler is configured, add that
                if (targetInvocationConfig.getRequestHandlers() != null) {
                    sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                            .getRequestHandlers()));
                    sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (targetInvocationConfig.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(targetInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    if (!(sourceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                            .getHeadInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        sourceInvocationConfig.setTargetInterceptor(targetInvocationConfig.getHeadInterceptor());
                    }
                }
            }

            for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration().getInvocationConfigurations()
                    .values()) {
                //FIXME should use target method, not sourceInvocationConfig.getMethod()
                TargetInvoker invoker = new ScopedJavaComponentInvoker(sourceInvocationConfig.getMethod(), targetContext, cacheable);
                sourceInvocationConfig.setTargetInvoker(invoker);
            }
        }
    }


    public static Map<Method, SourceInvocationConfiguration> createSourceInvocationConfigurations(Class<?> interfaze) {
        Map<Method, SourceInvocationConfiguration> invocations = new HashMap<Method, SourceInvocationConfiguration>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            invocations.put(method, new SourceInvocationConfigurationImpl(method));
        }
        return invocations;
    }

    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz, List<Injector> injectors) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr, null, injectors);
    }


}
