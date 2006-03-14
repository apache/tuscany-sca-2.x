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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.UnknownTypeException;
import org.apache.tuscany.core.builder.impl.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NonProxiedTargetFactory;
import org.apache.tuscany.core.injection.SDOObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.config.SystemContextFactory;
import org.apache.tuscany.core.system.context.SystemAggregateContextImpl;
import org.apache.tuscany.core.system.injection.AutowireObjectFactory;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Decorates components whose implementation type is a
 * {@link org.apache.tuscany.core.system.assembly.SystemImplementation} with the appropriate runtime configuration. This
 * builder handles both system aggregate components as well as system leaf or "simple" components. Consequently, both
 * simple and aggregate component types may be injected and autowired.
 * <p>
 * Note that system component references are not proxied.
 * 
 * @version $Rev$ $Date$
 */
public class SystemContextFactoryBuilder implements ContextFactoryBuilder<AggregateContext> {

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemContextFactoryBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject) throws BuilderException {
        if (!(modelObject instanceof Component)) {
            return;
        }
        Component component = (Component) modelObject;

        Class implClass = null;
        Scope scope = null;
        // Get the component implementation
        ComponentImplementation componentImplementation = component.getComponentImplementation();
        if (componentImplementation instanceof SystemImplementation && componentImplementation.getContextFactory() == null) {

            // The component is a system component, implemented by a Java class
            SystemImplementation javaImpl = (SystemImplementation) componentImplementation;
            if (componentImplementation.getComponentType().getServices() == null
                    || componentImplementation.getComponentType().getServices().size() < 1) {
                BuilderConfigException e = new BuilderConfigException("No service configured on component type");
                e.setIdentifier(component.getName());
                throw e;
            }
            implClass = javaImpl.getImplementationClass();
            Scope previous = null;
            scope = Scope.INSTANCE;
            List<Service> services = component.getComponentImplementation().getComponentType().getServices();
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
                if (scope != null && current != Scope.INSTANCE) {
                    scope = current;
                }
            }

        } else if (componentImplementation instanceof Module) {
            // FIXME this is a hack
            if (((Module) componentImplementation).getName().startsWith("org.apache.tuscany.core.system")) {
                // The component is a system module component, fix the implementation class to our implementation
                // of system module component context
                implClass = SystemAggregateContextImpl.class;
                scope = Scope.AGGREGATE;

            } else {

                // The component is an app module component, fix the implementation class to our implementation
                // of app module component context
                implClass = AggregateContextImpl.class;
                scope = Scope.AGGREGATE;

            }

        } else {
            return;
        }
        Set<Field> fields;
        Set<Method> methods;
        SystemContextFactory config = null;
        try {
            fields = JavaIntrospectionHelper.getAllFields(implClass);
            methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
            String name = component.getName();
            Constructor ctr = implClass.getConstructor((Class[]) null);
            config = new SystemContextFactory(name, JavaIntrospectionHelper.getDefaultConstructor(implClass),
                    scope);
            ContextObjectFactory contextFactory = new ContextObjectFactory(config);

            List<Injector> injectors = new ArrayList();

            // handle properties
            List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
            if (configuredProperties != null) {
                for (ConfiguredProperty property : configuredProperties) {
                    Injector injector = createPropertyInjector(property, fields, methods);
                    injectors.add(injector);
                }
            }

            // FIXME do not inject references on an application module yet
            if (implClass != AggregateContextImpl.class) {
                // handle references
                Map<String, ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences.values()) {
                        Injector injector = createReferenceInjector(reference, fields, methods, config);
                        injectors.add(injector);
                    }
                }
            }

            // create factory for the component implementation type
            EventInvoker initInvoker = null;
            boolean eagerInit = false;
            EventInvoker destroyInvoker = null;
            for (Field field : fields) {
                ComponentName compName = field.getAnnotation(ComponentName.class);
                if (compName != null) {
                    Injector injector = new FieldInjector(field, new SingletonObjectFactory(name));
                    injectors.add(injector);
                }
                Context context = field.getAnnotation(Context.class);
                if (context != null) {
                    Injector injector = new FieldInjector(field, contextFactory);
                    injectors.add(injector);
                }
                ParentContext parentField = field.getAnnotation(ParentContext.class);
                if (parentField != null) {
//                    if (!(parentContext instanceof AggregateContext)) {
//                        BuilderConfigException e = new BuilderConfigException("Component must be a child of");
//                        e.setIdentifier(AggregateContext.class.getName());
//                        throw e;
//                    }
                    Injector injector = new FieldInjector(field, contextFactory);
                    injectors.add(injector);
                }
                Autowire autowire = field.getAnnotation(Autowire.class);
                if (autowire != null) {
//                    if (!(parentContext instanceof AutowireContext)) {
//                        BuilderConfigException e = new BuilderConfigException("Parent context must implement");
//                        e.setIdentifier(AutowireContext.class.getName());
//                        throw e;
//                    }
//                    AutowireContext ctx = (AutowireContext) parentContext;
                    // for system aggregate context types, only allow autowire of certain types, otherwise we have a
                    // chicken-and-egg problem
                    if (SystemAggregateContext.class.isAssignableFrom(implClass)
                            && !(field.getType().equals(ConfigurationContext.class)
                                    || field.getType().equals(MonitorFactory.class)
                                    || field.getType().equals(RuntimeContext.class) || field.getType().equals(
                                    AutowireContext.class))) {
                        BuilderConfigException e = new BuilderConfigException("Illegal autowire type for system context");
                        e.setIdentifier(field.getType().getName());
                        throw e;
                    }

//                    Object o = ctx.resolveInstance(field.getType());
//                    if (autowire.required() && o == null) {
//                        BuilderConfigException e = new BuilderConfigException("No autowire found for field");
//                        e.setIdentifier(field.getName());
//                        throw e;
//                    }
                    Injector injector = new FieldInjector(field, new AutowireObjectFactory(field.getType(),autowire.required(),config));
                    injectors.add(injector);
                }
            }
            for (Method method : methods) {
                Init init = method.getAnnotation(Init.class);
                if (init != null && initInvoker == null) {
                    initInvoker = new MethodEventInvoker(method);
                    eagerInit = init.eager();
                    continue;
                }
                Destroy destroy = method.getAnnotation(Destroy.class);
                if (destroy != null && destroyInvoker == null) {
                    destroyInvoker = new MethodEventInvoker(method);
                    continue;
                }
                ComponentName compName = method.getAnnotation(ComponentName.class);
                if (compName != null) {
                    Injector injector = new MethodInjector(method, new SingletonObjectFactory(name));
                    injectors.add(injector);
                }
                Context context = method.getAnnotation(Context.class);
                if (context != null) {
                    Injector injector = new MethodInjector(method, contextFactory);
                    injectors.add(injector);
                }
                ParentContext parentMethod = method.getAnnotation(ParentContext.class);
                if (parentMethod != null) {
//                    if (!(parentContext instanceof AggregateContext)) {
//                        BuilderConfigException e = new BuilderConfigException("Component must be a child of ");
//                        e.setIdentifier(AggregateContext.class.getName());
//                        throw e;
//                    }
                    Injector injector = new MethodInjector(method, contextFactory);
                    injectors.add(injector);
                }
                Autowire autowire = method.getAnnotation(Autowire.class);
                if (autowire != null) {
//                    if (!(parentContext instanceof AutowireContext)) {
//                        BuilderConfigException e = new BuilderConfigException("Parent context must implement)");
//                        e.setIdentifier(AutowireContext.class.getName());
//                        throw e;
//                    }
                    if (method.getParameterTypes() == null || method.getParameterTypes().length != 1) {
                        BuilderConfigException e = new BuilderConfigException("Autowire setter methods must take one parameter");
                        e.setIdentifier(method.getName());
                        throw e;
                    }
//                    AutowireContext ctx = (AutowireContext) parentContext;
                    Class paramType = method.getParameterTypes()[0];
                    // for system aggregate context types, only allow autowire of certain types, otherwise we have a
                    // chicken-and-egg problem
                    if (SystemAggregateContext.class.isAssignableFrom(implClass)
                            && !(paramType.equals(ConfigurationContext.class) || paramType.equals(MonitorFactory.class)
                                    || paramType.equals(RuntimeContext.class) || paramType.equals(AutowireContext.class))) {
                        BuilderConfigException e = new BuilderConfigException("Illegal autowire type for system context");
                        e.setIdentifier(paramType.getName());
                        throw e;
                    }
//                    Object o = ctx.resolveInstance(paramType);
//                    if (autowire.required() && o == null) {
//                        BuilderConfigException e = new BuilderConfigException("No autowire found for method ");
//                        e.setIdentifier(method.getName());
//                        throw e;
//                    }

                    Injector injector = new MethodInjector(method, new AutowireObjectFactory(paramType,autowire.required(), config));
                    injectors.add(injector);
                }
            }
            // decorate the logical model
            config.setSetters(injectors);
            config.setEagerInit(eagerInit);
            config.setInitInvoker(initInvoker);
            config.setDestroyInvoker(destroyInvoker);
            componentImplementation.setContextFactory(config);
        } catch (BuilderConfigException e) {
            e.addContextName(component.getName());
            // e.addContextName(parentContext.getName());
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

    // ----------------------------------
    // Private methods
    // ----------------------------------

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
            method = JavaIntrospectionHelper.findClosestMatchingMethod(propName, new Class[] { type }, methods);
            if (method == null) {
                throw new NoAccessorException(propName);
            }
        }
        Injector injector = null;
        // FIXME support types other than String
        if (value instanceof DataObject) {
            if (field != null) {
                injector = new FieldInjector(field, new SDOObjectFactory((DataObject) value));
            } else {
                injector = new MethodInjector(method, new SDOObjectFactory((DataObject) value));
            }
        } else if (JavaIntrospectionHelper.isImmutable(type)) {
            if (field != null) {
                injector = new FieldInjector(field, new SingletonObjectFactory(value));
            } else {
                injector = new MethodInjector(method, new SingletonObjectFactory(value));
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
     * Creates object factories that resolve target(s) of a reference and an <code>Injector</code> responsible for
     * injecting them into the reference
     */
    private Injector createReferenceInjector(ConfiguredReference reference, Set<Field> fields, Set<Method> methods,
            ContextResolver resolver) {

        List<ObjectFactory> objectFactories = new ArrayList();
        String refName = reference.getReference().getName();
        Class refClass = reference.getReference().getServiceContract().getInterface();
        for (ConfiguredService configuredService : reference.getTargetConfiguredServices()) {
            String targetCompName = configuredService.getAggregatePart().getName();
            String targetSerivceName = configuredService.getService().getName();
            QualifiedName qName = new QualifiedName(targetCompName + QualifiedName.NAME_SEPARATOR + targetSerivceName);
            Class interfaze = reference.getReference().getServiceContract().getInterface();
            objectFactories.add(new NonProxiedTargetFactory(configuredService, resolver));
        }
        boolean multiplicity = reference.getReference().getMultiplicity() == Multiplicity.ONE_N
                || reference.getReference().getMultiplicity() == Multiplicity.ZERO_N;
        return createInjector(refName, refClass, multiplicity, objectFactories, fields, methods);

    }

    /**
     * Creates an <code>Injector</code> for an object factories associated with a reference.
     */
    private Injector createInjector(String refName, Class refClass, boolean multiplicity, List<ObjectFactory> objectFactories,
            Set<Field> fields, Set<Method> methods) throws NoAccessorException, BuilderConfigException {
        Field field = null;
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
                method = JavaIntrospectionHelper.findClosestMatchingMethod(refName, new Class[] { refClass }, methods);
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
