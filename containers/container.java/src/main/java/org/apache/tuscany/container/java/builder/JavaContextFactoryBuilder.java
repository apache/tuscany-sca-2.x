package org.apache.tuscany.container.java.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.JavaExtensibilityHelper;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.extension.ContextFactoryBuilderSupport;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Builds context factories for component implementations that map to {@link
 * org.apache.tuscany.container.java.assembly.JavaImplementation}. The logical model is then decorated with
 * the runtime configuration.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 * @see org.apache.tuscany.core.builder.ContextFactory
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaContextFactoryBuilder extends ContextFactoryBuilderSupport<JavaImplementation> {

    /**
     * Default constructor
     */
    public JavaContextFactoryBuilder() {
        super();
    }

    /**
     * Constructs a new instance
     *
     * @param wireFactoryService the system service responsible for creating wire factories
     */
    public JavaContextFactoryBuilder(WireFactoryService wireFactoryService) {
        super(wireFactoryService);
    }

    @SuppressWarnings("unchecked")
    protected ContextFactory createContextFactory(String name, JavaImplementation javaImpl, Scope scope) {
        Class implClass = null;
        JavaContextFactory contextFactory;
        try {
            implClass = javaImpl.getImplementationClass();

            contextFactory = new JavaContextFactory(name, JavaIntrospectionHelper
                    .getDefaultConstructor(implClass), scope);

            List<Injector> injectors = new ArrayList<Injector>();
            EventInvoker<Object> initInvoker;
            boolean eagerInit;
            EventInvoker<Object> destroyInvoker;
            ContextObjectFactory contextObjectFactory = new ContextObjectFactory(contextFactory);
            JavaExtensibilityElement element = JavaExtensibilityHelper.getExtensibilityElement(javaImpl.getComponentInfo());
            AccessibleObject ao = element.getComponentName();
            if (ao instanceof Field) {
                Injector injector = new FieldInjector((Field) ao, new SingletonObjectFactory<Object>(name));
                injectors.add(injector);
            } else if (ao instanceof Method) {
                Injector injector = new MethodInjector((Method) ao, new SingletonObjectFactory<Object>(name));
                injectors.add(injector);
            }
            ao = element.getContext();
            if (ao instanceof Field) {
                Injector injector = new FieldInjector((Field) ao, contextObjectFactory);
                injectors.add(injector);
            } else if (ao instanceof Method) {
                Injector injector = new MethodInjector((Method) ao, contextObjectFactory);
                injectors.add(injector);
            }
            initInvoker = element.getInit();
            eagerInit = element.isEagerInit();
            destroyInvoker = element.getDestroy();
            if (initInvoker != null) {
                contextFactory.setEagerInit(eagerInit);
                contextFactory.setInitInvoker(initInvoker);
            }
            if (destroyInvoker != null) {
                contextFactory.setDestroyInvoker(destroyInvoker);
            }
            contextFactory.setSetters(injectors);
            return contextFactory;
        } catch (BuilderException e) {
            e.addContextName(name);
            throw e;
        } catch (NoSuchMethodException e) {
            BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
            ce.setIdentifier(implClass.getName());
            ce.addContextName(name);
            throw ce;
        }
    }

}
