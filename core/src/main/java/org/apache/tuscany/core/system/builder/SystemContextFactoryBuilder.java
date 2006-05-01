/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.UnknownTypeException;
import org.apache.tuscany.core.builder.impl.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.extension.config.InjectorExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.ComponentNameExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.ContextExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.DestroyInvokerExtensibilityElement;
import org.apache.tuscany.core.extension.config.extensibility.InitInvokerExtensibilityElement;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NonProxiedTargetFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.SystemModule;
import org.apache.tuscany.core.system.config.SystemContextFactory;
import org.apache.tuscany.core.system.config.SystemInjectorExtensibilityElement;
import org.apache.tuscany.core.system.config.extensibility.MonitorExtensibilityElement;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;

/**
 * Decorates components whose implementation type is a {@link org.apache.tuscany.core.system.assembly.SystemImplementation}
 * with the appropriate runtime configuration. This builder handles both system composite components as well
 * as system leaf or "simple" components. Consequently, both simple and composite component types may be
 * injected and autowired.
 * <p/>
 * Note that system component references are not proxied.
 *
 * @version $Rev$ $Date$
 */
public class SystemContextFactoryBuilder implements ContextFactoryBuilder {

    private final MonitorFactory monitorFactory;

    public SystemContextFactoryBuilder(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    @SuppressWarnings("unchecked")
    public void build(AssemblyObject modelObject) throws BuilderException {
        if (!(modelObject instanceof Component)) {
            return;
        }
        Component component = (Component) modelObject;

        Class implClass;
        Scope scope;
        // Get the component implementation
        Implementation componentImplementation = component.getImplementation();
        if (componentImplementation instanceof SystemImplementation
                && component.getContextFactory() == null) {

            // The component is a system component, implemented by a Java class
            SystemImplementation implementation = (SystemImplementation) componentImplementation;
            if (componentImplementation.getComponentInfo().getServices() == null
                    || componentImplementation.getComponentInfo().getServices().size() < 1) {
                BuilderConfigException e = new BuilderConfigException("No service configured on component type");
                e.setIdentifier(component.getName());
                throw e;
            }
            implClass = implementation.getImplementationClass();
            Scope previous = null;
            scope = Scope.MODULE;
            List<Service> services = component.getImplementation().getComponentInfo().getServices();
            for (Service service : services) {
                // calculate and validate the scope of the component; ensure that all service scopes are the same unless
                // a scope is stateless
                Scope current = service.getServiceContract().getScope();
                if (previous != null && current != null && current != previous
                        && (current != Scope.INSTANCE && previous != Scope.INSTANCE)) {
                    BuilderException e = new BuilderConfigException("Incompatible scopes specified for services on component");
                    e.setIdentifier(component.getName());
                    throw e;
                }
                if (current != null && current != Scope.MODULE) {
                    scope = current;
                }
            }

        } else if (componentImplementation instanceof Composite) {
            implClass = ((Composite) componentImplementation).getImplementationClass();
            if (implClass == null) {
                // FIXME this is a hack
                if (((Module) componentImplementation).getName().startsWith("org.apache.tuscany.core.system"))
                {
                    // The component is a system module component, fix the implementation class to our implementation
                    // of system module component context
                    implClass = SystemCompositeContextImpl.class;
                } else if (componentImplementation instanceof SystemModule) {
                    implClass = SystemCompositeContextImpl.class;
                } else {
                    // The component is an app module component, fix the implementation class to our implementation
                    // of app module component context
                    //FIXME this should be extensible, i.e. the model should specify the impl class of the module
                    implClass = CompositeContextImpl.class;
                }
                //END hack
            }
            scope = Scope.AGGREGATE;
        } else {
            return;
        }
        Set<Field> fields;
        Set<Method> methods;
        SystemContextFactory contextFactory;
        try {
            fields = JavaIntrospectionHelper.getAllFields(implClass);
            methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
            String name = component.getName();
            if (componentImplementation instanceof Module) {
                Module module = (Module) componentImplementation;
                contextFactory = new SystemContextFactory(name, module, JavaIntrospectionHelper.getDefaultConstructor(implClass), scope);

            } else {
                contextFactory = new SystemContextFactory(name, JavaIntrospectionHelper.getDefaultConstructor(implClass), scope);
            }

            //ContextObjectFactory contextObjectFactory = new ContextObjectFactory(contextFactory);

            List<Injector> injectors = new ArrayList<Injector>();

            // handle properties
            List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
            if (configuredProperties != null) {
                for (ConfiguredProperty property : configuredProperties) {
                    Injector injector = createPropertyInjector(property, fields, methods);
                    injectors.add(injector);
                }
            }

            // FIXME do not inject references on an application module yet
            if (implClass != CompositeContextImpl.class) {
                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        Injector injector = createReferenceInjector(reference, fields, methods, contextFactory);
                        injectors.add(injector);
                    }
                }
            }
            List<Object> elements = componentImplementation.getComponentInfo().getExtensibilityElements();
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
                } else if (element instanceof InjectorExtensibilityElement) {
                    InjectorExtensibilityElement injectorElement = (InjectorExtensibilityElement) element;
                    injectors.add(injectorElement.getInjector(contextFactory));
                } else if (element instanceof SystemInjectorExtensibilityElement) {
                    SystemInjectorExtensibilityElement injectorElement = (SystemInjectorExtensibilityElement) element;
                    injectors.add(injectorElement.getInjector(contextFactory));
                } else if (element instanceof MonitorExtensibilityElement) {
                    MonitorExtensibilityElement monitorElement = (MonitorExtensibilityElement) element;
                    injectors.add(monitorElement.getInjector(monitorFactory));
                }
            }
            contextFactory.setSetters(injectors);
            // decorate the logical model
            component.setContextFactory(contextFactory);
        } catch (BuilderConfigException e) {
            e.addContextName(component.getName());
            throw e;
        } catch (NoSuchMethodException e) {
            BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
            ce.setIdentifier(implClass.getName());
            throw ce;
        } catch (FactoryInitException e) {
            BuilderConfigException ce = new BuilderConfigException("Error building component", e);
            ce.addContextName(component.getName());
            throw ce;
        }
    }

    /**
     * Creates an <code>Injector</code> for component properties
     */
    private Injector createPropertyInjector(ConfiguredProperty property, Set<Field> fields, Set<Method> methods)
            throws NoAccessorException {
        Object value = property.getValue();
        String propName = property.getProperty().getName();
        // @FIXME is this how to get property type of object
        Class type = value.getClass();

        // There is no efficient way to do this
        Method method = null;
        Field field = JavaIntrospectionHelper.findClosestMatchingField(propName, type, fields);
        if (field == null) {
            method = JavaIntrospectionHelper.findClosestMatchingMethod(propName, new Class[]{type}, methods);
            if (method == null) {
                throw new NoAccessorException(propName);
            }
        }
        Injector injector;
        // FIXME support types other than String
        if (JavaIntrospectionHelper.isImmutable(type)) {
            if (field != null) {
                injector = new FieldInjector(field, new SingletonObjectFactory<Object>(value));
            } else {
                injector = new MethodInjector(method, new SingletonObjectFactory<Object>(value));
            }
        } else {
            if (field != null) {
                throw new UnknownTypeException(field.getName());
            } else {
                throw new UnknownTypeException(method.getName());
            }
        }
        return injector;

    }

    /**
     * Creates object factories that resolve target(s) of a reference and an <code>Injector</code> responsible
     * for injecting them into the reference
     */
    private Injector createReferenceInjector(ConfiguredReference reference, Set<Field> fields, Set<Method> methods,
                                             ContextResolver resolver) {

        List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
        String refName = reference.getPort().getName();
        Class refClass = reference.getPort().getServiceContract().getInterface();
        for (ConfiguredService configuredService : reference.getTargetConfiguredServices()) {
            objectFactories.add(new NonProxiedTargetFactory(configuredService, resolver));
        }
        boolean multiplicity = reference.getPort().getMultiplicity() == Multiplicity.ONE_N
                || reference.getPort().getMultiplicity() == Multiplicity.ZERO_N;
        return createInjector(refName, refClass, multiplicity, objectFactories, fields, methods);

    }

    /**
     * Creates an <code>Injector</code> for an object factories associated with a reference.
     */
    private Injector createInjector(String refName, Class refClass, boolean multiplicity, List<ObjectFactory> objectFactories,
                                    Set<Field> fields, Set<Method> methods) throws NoAccessorException, BuilderConfigException {
        Field field;
        Method method = null;
        if (multiplicity) {
            // since this is a multiplicity, we cannot match on business interface type, so scan through the fields,
            // matching on name and List or Array
            field = JavaIntrospectionHelper.findMultiplicityFieldByName(refName, fields);
            if (field == null) {
                // No fields found. Again, since this is a multiplicity, we cannot match on interface type, so
                // scan through the fields, matching on name and List or Array
                method = JavaIntrospectionHelper.findMultiplicityMethodByName(refName, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            if (field != null) {
                // for multiplicities, we need to inject the target or targets using an object factory
                // which first delegates to create the proxies and then returns them in the appropriate List or array
                // type
                if (field.getType().isArray()) {
                    injector = new FieldInjector(field, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new FieldInjector(field, new ListMultiplicityObjectFactory(objectFactories));
                }
            } else {
                if (method.getParameterTypes()[0].isArray()) {
                    injector = new MethodInjector(method, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new MethodInjector(method, new ListMultiplicityObjectFactory(objectFactories));
                }
            }
            return injector;
        } else {
            field = JavaIntrospectionHelper.findClosestMatchingField(refName, refClass, fields);
            if (field == null) {
                method = JavaIntrospectionHelper.findClosestMatchingMethod(refName, new Class[]{refClass}, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            if (field != null) {
                injector = new FieldInjector(field, objectFactories.get(0));
            } else {
                injector = new MethodInjector(method, objectFactories.get(0));
            }
            return injector;
        }
    }

}
