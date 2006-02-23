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
import java.util.Set;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.UnknownTypeException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ReferenceTargetFactory;
import org.apache.tuscany.core.injection.SDOObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.config.SystemComponentRuntimeConfiguration;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Scope;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Decorates components whose implementation type is a
 * {@link org.apache.tuscany.core.system.assembly.SystemImplementation} with the appropriate runtime configuration.
 * System components are not proxied.
 * 
 * @version $Rev$ $Date$
 */
public class SystemComponentContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemComponentContextBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void build(AssemblyModelObject modelObject, AggregateContext parentContext) throws BuilderException {
        if (!(modelObject instanceof Component)) {
            return;
        }
        Component component = (Component) modelObject;
        if (component.getComponentImplementation() instanceof SystemImplementation
                && component.getComponentImplementation().getRuntimeConfiguration() == null) {
            SystemImplementation javaImpl = (SystemImplementation) component.getComponentImplementation();
            // FIXME scope
            Scope scope = component.getComponentImplementation().getComponentType().getServices().get(0).getServiceContract()
                    .getScope();
            Class implClass = null;
            Set<Field> fields;
            Set<Method> methods;
            try {
                implClass = javaImpl.getImplementationClass();
                fields = JavaIntrospectionHelper.getAllFields(implClass);
                methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
                String name = component.getName();
                Constructor ctr = implClass.getConstructor((Class[]) null);

                List<Injector> injectors = new ArrayList();

                // handle properties
                List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
                if (configuredProperties != null) {
                    for (ConfiguredProperty property : configuredProperties) {
                        Injector injector = createPropertyInjector(property, fields, methods);
                        injectors.add(injector);
                    }
                }

                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        Injector injector = createReferenceInjector(parentContext.getName(), component.getName(), parentContext,
                                reference, fields, methods);
                        injectors.add(injector);
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
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory(parentContext));
                        injectors.add(injector);
                    }
                    ParentContext parentField = field.getAnnotation(ParentContext.class);
                    if (parentField != null) {
                        if (!(parentContext instanceof AggregateContext)) {
                            BuilderConfigException e = new BuilderConfigException("Component must be a child of");
                            e.setIdentifier(AggregateContext.class.getName());
                            throw e;
                        }
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory((parentContext)));
                        injectors.add(injector);
                    }
                    Autowire autowire = field.getAnnotation(Autowire.class);
                    if (autowire != null) {
                        if (!(parentContext instanceof AutowireContext)) {
                            BuilderConfigException e = new BuilderConfigException("Parent context must implement");
                            e.setIdentifier(AutowireContext.class.getName());
                            throw e;
                        }
                        AutowireContext ctx = (AutowireContext) parentContext;
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

                        Object o = ctx.resolveInstance(field.getType());
                        if (autowire.required() && o == null) {
                            BuilderConfigException e = new BuilderConfigException("No autowire found for field");
                            e.setIdentifier(field.getName());
                            throw e;
                        }
                        Injector injector = new FieldInjector(field, new SingletonObjectFactory(o));
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
                        Injector injector = new MethodInjector(method, new SingletonObjectFactory(parentContext));
                        injectors.add(injector);
                    }
                    ParentContext parentMethod = method.getAnnotation(ParentContext.class);
                    if (parentMethod != null) {
                        if (!(parentContext instanceof AggregateContext)) {
                            BuilderConfigException e = new BuilderConfigException("Component must be a child of ");
                            e.setIdentifier(AggregateContext.class.getName());
                            throw e;
                        }
                        Injector injector = new MethodInjector(method, new SingletonObjectFactory((parentContext)));
                        injectors.add(injector);
                    }
                    Autowire autowire = method.getAnnotation(Autowire.class);
                    if (autowire != null) {
                        if (!(parentContext instanceof AutowireContext)) {
                            BuilderConfigException e = new BuilderConfigException("Parent context must implement)");
                            e.setIdentifier(AutowireContext.class.getName());
                            throw e;
                        }
                        if (method.getParameterTypes() == null || method.getParameterTypes().length != 1) {
                            BuilderConfigException e = new BuilderConfigException(
                                    "Autowire setter methods must take one parameter");
                            e.setIdentifier(method.getName());
                            throw e;
                        }
                        AutowireContext ctx = (AutowireContext) parentContext;
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
                        Object o = ctx.resolveInstance(paramType);
                        if (autowire.required() && o == null) {
                            BuilderConfigException e = new BuilderConfigException("No autowire found for method ");
                            e.setIdentifier(method.getName());
                            throw e;
                        }

                        Injector injector = new MethodInjector(method, new SingletonObjectFactory(o));
                        injectors.add(injector);
                    }
                }
                // decorate the logical model
                SystemComponentRuntimeConfiguration config = new SystemComponentRuntimeConfiguration(name,
                        JavaIntrospectionHelper.getDefaultConstructor(implClass), injectors, eagerInit, initInvoker,
                        destroyInvoker, scope);
                component.getComponentImplementation().setRuntimeConfiguration(config);
            } catch (BuilderConfigException e) {
                e.addContextName(component.getName());
                e.addContextName(parentContext.getName());
                throw e;
            } catch (NoSuchMethodException e) {
                BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
                ce.setIdentifier(implClass.getName());
                throw ce;
            }
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
     * Creates an <code>Injector</code> for service references
     */
    private Injector createReferenceInjector(String moduleName, String componentName, AggregateContext parentContext,
            ConfiguredReference reference, Set<Field> fields, Set<Method> methods) throws NoAccessorException,
            BuilderConfigException {
        String refName = reference.getReference().getName();
        List<ConfiguredService> services = reference.getTargetConfiguredServices();
        Class type;
        if (services.size() == 1) {
            // get the interface
            type = reference.getReference().getServiceContract().getInterface();
        } else {
            // FIXME do we support arrays?
            type = List.class;
        }
        Method method = null;
        Field field = JavaIntrospectionHelper.findClosestMatchingField(refName, type, fields);
        if (field == null) {
            method = JavaIntrospectionHelper.findClosestMatchingMethod(refName, new Class[] { type }, methods);
            if (method == null) {
                throw new NoAccessorException(refName);
            }
        }
        Injector injector;
        try {
            if (field != null) {
                injector = new FieldInjector(field, new ReferenceTargetFactory(reference, parentContext));
            } else {
                injector = new MethodInjector(method, new ReferenceTargetFactory(reference, parentContext));
            }
        } catch (ObjectCreationException e) {
            BuilderConfigException ce = new BuilderConfigException("Error configuring reference", e);
            ce.setIdentifier(refName);
            throw ce;
        }
        return injector;

    }

}
