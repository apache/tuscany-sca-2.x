package org.apache.tuscany.container.java.builder;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.extension.ContextFactoryBuilderSupport;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.Scope;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Builds context factories for component implementations that map to {@link org.apache.tuscany.container.java.assembly.JavaImplementation}.
 * The logical model is then decorated with the runtime configuration.
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
        Set<Field> fields;
        Set<Method> methods;
        JavaContextFactory contextFactory;
        try {
            implClass = javaImpl.getImplementationClass();
            // TODO refactor fields and methods lookup since done in factory as well or elsewhere
            fields = JavaIntrospectionHelper.getAllFields(implClass);
            methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
            contextFactory = new JavaContextFactory(name, JavaIntrospectionHelper
                    .getDefaultConstructor(implClass), scope);

            List<Injector> injectors = new ArrayList<Injector>();

            EventInvoker<Object> initInvoker = null;
            boolean eagerInit = false;
            EventInvoker<Object> destroyInvoker = null;
            ContextObjectFactory contextObjectFactory = new ContextObjectFactory(contextFactory);
            for (Field field : fields) {
                ComponentName compName = field.getAnnotation(ComponentName.class);
                if (compName != null) {
                    Injector injector = new FieldInjector(field, new SingletonObjectFactory<Object>(name));
                    injectors.add(injector);
                }
                Context context = field.getAnnotation(Context.class);
                if (context != null) {
                    Injector injector = new FieldInjector(field, contextObjectFactory);
                    injectors.add(injector);
                }
            }
            for (Method method : methods) {
                Init init = method.getAnnotation(Init.class);
                if (init != null && initInvoker == null) {
                    initInvoker = new MethodEventInvoker<Object>(method);
                    eagerInit = init.eager();
                    continue;
                }
                // TODO spec - should we allow the same method to have @init and @destroy?
                Destroy destroy = method.getAnnotation(Destroy.class);
                if (destroy != null && destroyInvoker == null) {
                    destroyInvoker = new MethodEventInvoker<Object>(method);
                    continue;
                }
                ComponentName compName = method.getAnnotation(ComponentName.class);
                if (compName != null) {
                    Injector injector = new MethodInjector(method, new SingletonObjectFactory<Object>(name));
                    injectors.add(injector);
                }
                Context context = method.getAnnotation(Context.class);
                if (context != null) {
                    Injector injector = new MethodInjector(method, contextObjectFactory);
                    injectors.add(injector);
                }
            }
            contextFactory.setSetters(injectors);
            contextFactory.setEagerInit(eagerInit);
            contextFactory.setInitInvoker(initInvoker);
            contextFactory.setDestroyInvoker(destroyInvoker);
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
