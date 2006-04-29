package org.apache.tuscany.container.java.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.core.extension.config.extensibility.ComponentNameExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.ContextExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.DestroyInvokerExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.InitInvokerExtensibilityElement;
import org.apache.tuscany.core.extension.config.InjectorExtensibilityElement;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.extension.ContextFactoryBuilderSupport;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
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
            List<Object> elements = javaImpl.getComponentInfo().getExtensibilityElements();
            for (Object element : elements) {
                if (element instanceof InitInvokerExtensibilityElement) {
                    InitInvokerExtensibilityElement invokerElement = (InitInvokerExtensibilityElement) element;
                    EventInvoker<Object> initInvoker = invokerElement.getEventInvoker();
                    boolean eagerInit = invokerElement.isEager();
                    contextFactory.setEagerInit(eagerInit);
                    contextFactory.setInitInvoker(initInvoker);
                } else if (element instanceof DestroyInvokerExtensibilityElement) {
                    DestroyInvokerExtensibilityElement invokerElement = (DestroyInvokerExtensibilityElement) element;
                    EventInvoker<Object> destroyInvoker = invokerElement.getEventInvoker();
                    contextFactory.setDestroyInvoker(destroyInvoker);
                } else if (element instanceof ComponentNameExtensibilityElement) {
                    ComponentNameExtensibilityElement nameElement = (ComponentNameExtensibilityElement) element;
                    injectors.add(nameElement.getEventInvoker(name));
                } else if (element instanceof ContextExtensibilityElement) {
                    ContextExtensibilityElement contextElement = (ContextExtensibilityElement) element;
                    injectors.add(contextElement.getInjector(contextFactory));
                }else if (element instanceof InjectorExtensibilityElement){
                    InjectorExtensibilityElement injectorElement = (InjectorExtensibilityElement)element;
                    injectors.add(injectorElement.getInjector(contextFactory));
                }
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
