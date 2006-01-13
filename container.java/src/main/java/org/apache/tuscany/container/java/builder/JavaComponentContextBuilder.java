package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.JavaComponentRuntimeConfiguration;
import org.apache.tuscany.container.java.injection.ReferenceProxyTargetFactory;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SDOObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import commonj.sdo.DataObject;

/**
 * Decorates components whose implementation type is a {@link org.apache.tuscany.container.java.assembly.JavaImplementation} with
 * the appropriate runtime configuration
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    private String name;

    private final List<Injector> setters = new ArrayList();

    private AggregateContext parentContext;

    private AssemblyModelObject modelObject;
    
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JavaComponentContextBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setModelObject(AssemblyModelObject modelObject) {
        this.modelObject = modelObject;
    }

    public void setParentContext(AggregateContext context) {
        parentContext = context;
    }

    public void build() throws BuilderException {
        if(!(modelObject instanceof SimpleComponent)){
            return;
        }
        SimpleComponent component = (SimpleComponent)modelObject;
        if (component.getComponentImplementation() instanceof JavaImplementation) {
            JavaImplementation javaImpl = (JavaImplementation) component.getComponentImplementation();
            // FIXME scope
            ScopeEnum scope = component.getComponentImplementation().getServices().get(0).getInterfaceContract().getScope();
            Class implClass = null;
            Set<Field> fields;
            Set<Method> methods;
            try {
                implClass = JavaIntrospectionHelper.loadClass(javaImpl.getClass_());
                fields = JavaIntrospectionHelper.getAllFields(implClass);
                methods = JavaIntrospectionHelper.getAllUniqueMethods(implClass);
                name = component.getName();
                Constructor ctr = implClass.getConstructor((Class[]) null);

                List<Injector> injectors = new ArrayList();

                // handle properties
                List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
                // FIXME should return empty properties - does it?
                if (configuredProperties != null) {
                    for (ConfiguredProperty property : configuredProperties) {
                        Injector injector = createPropertyInjector(property, fields, methods);
                        injectors.add(injector);
                    }
                }

                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                // FIXME should return empty refs - does it?
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        Injector injector = createReferenceInjector(parentContext.getName(), component.getName(),
                                reference, fields, methods);
                        injectors.add(injector);
                    }
                }

                /*
                 * TODO if the specs support constructor injection, this would be determined here from the property config
                 */
                // create factory for the component implementation type
                EventInvoker initInvoker = null;
                boolean eagerInit = false;
                EventInvoker destroyInvoker = null;
                // FIXME this should be run as part of the LCM load
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
                }
                for (Method method : methods) {
                    // FIXME Java5
                    Init init = method.getAnnotation(Init.class);
                    if (init != null && initInvoker == null) {
                        initInvoker = new MethodEventInvoker(method);
                        eagerInit = init.eager();
                        continue;
                    }
                    // @spec - should we allow the same method to have @init and
                    // @destroy?
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
                }
                // FIXME End this should be run as part of the LCM load

                // decorate the logical model
                JavaComponentRuntimeConfiguration config = new JavaComponentRuntimeConfiguration(name, JavaIntrospectionHelper
                        .getDefaultConstructor(implClass), injectors, eagerInit, initInvoker, destroyInvoker, scope.getValue());
                component.getComponentImplementation().setRuntimeConfiguration(config);
            }catch (BuilderException e){
                e.addContextName(component.getName());
                e.addContextName(parentContext.getName());
                throw e;
            } catch (ClassNotFoundException e) {
                BuilderException be = new BuilderConfigException(e);
                be.addContextName(component.getName());
                be.addContextName(parentContext.getName());
                throw be;
            } catch (NoSuchMethodException e) {
                BuilderConfigException ce = new BuilderConfigException("Class does not have a no-arg constructor", e);
                ce.setIdentifier(implClass.getName());
                ce.addContextName(component.getName());
                ce.addContextName(parentContext.getName());
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
        }
        return injector;

    }

    /**
     * Creates an <code>Injector</code> for service references
     */
    private Injector createReferenceInjector(String moduleName, String componentName, ConfiguredReference reference,
            Set<Field> fields, Set<Method> methods) throws NoAccessorException, BuilderConfigException {
        String refName = reference.getReference().getName();
        List<ConfiguredService> services = reference.getConfiguredServices();
        Class type;
        // FIXME added the size check - do we need to do this?
        if (services.size() == 1) {
            // get the interface
            type = reference.getReference().getInterfaceContract().getInterfaceType().getInstanceClass();
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
                injector = new FieldInjector(field, new ReferenceProxyTargetFactory(reference));
            } else {
                injector = new MethodInjector(method, new ReferenceProxyTargetFactory(reference));
            }
        } catch (FactoryInitException e) {
            BuilderConfigException ce = new BuilderConfigException("Error configuring reference", e);
            ce.setIdentifier(refName);
            throw ce;
        }
        return injector;

    }

}
