/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.implementation.POJOPhysicalComponentBuilder;
import org.apache.tuscany.core.injection.CallbackWireObjectFactory2;
import org.apache.tuscany.core.injection.InstanceObjectFactory;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import static org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType.CALLBACK;
import static org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType.REFERENCE;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;
import org.apache.tuscany.core.wire.WireObjectFactory2;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.builder.physical.WireAttachException;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;

/**
 * The physical component builder for Java implementation types. Responsible for creating the Component runtime artifact
 * from a physical component definition
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class for the defined component
 */
public class JavaPhysicalComponentBuilder<T>
    extends POJOPhysicalComponentBuilder<JavaPhysicalComponentDefinition<T>, JavaComponent<T>>
    implements WireAttacher<JavaComponent, JavaPhysicalWireSourceDefinition, JavaPhysicalWireTargetDefinition> {

    // Classloader registry
    private ClassLoaderRegistry classLoaderRegistry;

    private ProxyService proxyService;

    public JavaPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry) {
        super(builderRegistry, scopeRegistry);
    }

    @Reference
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Injects classloader registry.
     *
     * @param classLoaderRegistry Class loader registry.
     */
    @Reference
    public void setClassLoaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Builds a component from its physical component definition.
     *
     * @param componentDefinition Physical component definition of the component to be built.
     * @return A component instance that is ready to go live.
     * @throws BuilderException If unable to build the component.
     */
    public JavaComponent<T> build(JavaPhysicalComponentDefinition<T> componentDefinition) throws BuilderException {

        URI componentId = componentDefinition.getComponentId();
        InstanceFactoryProvider<T> provider = componentDefinition.getProvider();
        JavaComponent<T> component = new JavaComponent<T>(componentId, provider, null, null, 0, -1, -1);

        setInstanceFactoryClass(componentDefinition, component);

        return component;
    }

    /*
     * Sets the instance factory class.
     */
    private void setInstanceFactoryClass(JavaPhysicalComponentDefinition componentDefinition, JavaComponent component) {
/*
        // TODO use MPCL to load IF class
        URI classLoaderId = componentDefinition.getClassLoaderId();
        byte[] instanceFactoryByteCode = componentDefinition.getInstanceFactoryByteCode(); //NOPMD
        ClassLoader appCl = classLoaderRegistry.getClassLoader(classLoaderId); //NOPMD
        ClassLoader systemCl = getClass().getClassLoader(); //NOPMD        
        ClassLoader mpcl = null; //NOPMD
        Class<InstanceFactory<?>> instanceFactoryClass = null;
        component.setInstanceFactoryClass(instanceFactoryClass);
*/
    }

    /**
     * Attaches the source to the component.
     *
     * @param source     the source component to attach to
     * @param target     the source component
     * @param wire       the wire for the callback
     * @param definition the attach metadata
     */
    @SuppressWarnings({"unchecked"})
    public void attach(JavaComponent source,
                       Component target,
                       Wire wire,
                       JavaPhysicalWireSourceDefinition definition) {
        URI sourceUri = definition.getUri();
        InjectionSource referenceSource = new InjectionSource(REFERENCE, sourceUri.getFragment());
        Class<?> type = source.getMemberType(referenceSource);
        if (definition.isOptimizable()) {
            // FIXME if possible, this is not clean
            assert target instanceof AtomicComponent;
            ScopeContainer container = target.getScopeContainer();
            ObjectFactory<?> factory = new InstanceObjectFactory((AtomicComponent) target, container);
            source.setObjectFactory(referenceSource, factory);
        } else {
            ObjectFactory<?> factory = new WireObjectFactory2(type, definition.isConversational(), wire, proxyService);
            source.setObjectFactory(referenceSource, factory);
            if (!wire.getCallbackInvocationChains().isEmpty()) {
                URI callbackUri = definition.getCallbackUri();
                InjectionSource callbackSource = new InjectionSource(CALLBACK, callbackUri.getFragment());
                Class<?> callbackType = source.getMemberType(callbackSource);
                ObjectFactory<?> callbackFactory = new CallbackWireObjectFactory2(callbackType, proxyService);
                source.setObjectFactory(callbackSource, callbackFactory);
            }
        }
    }

    /**
     * Attaches the target to the component.
     *
     * @param component Component.
     * @param wire      the wire to attach
     * @param target    Target.
     */
    public void attach(JavaComponent component, Wire wire, JavaPhysicalWireTargetDefinition target)
        throws WireAttachException {
        ScopeContainer scopeContainer = component.getScopeContainer();
        Class<?> implementationClass = component.getImplementationClass();
        ClassLoader loader = implementationClass.getClassLoader();
        // attach the invoker interceptor to forward invocation chains
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getPhysicalInvocationChains()
            .entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            InvocationChain chain = entry.getValue();
            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            assert loader != null;
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    paramTypes[i] = loader.loadClass(param);
                } catch (ClassNotFoundException e) {
                    URI sourceUri = wire.getSourceUri();
                    URI targetUri = wire.getTargetUri();
                    throw new WireAttachException("Implementation class not found", sourceUri, targetUri, e);
                }
            }
            Method method;
            try {
                method = implementationClass.getMethod(operation.getName(), paramTypes);
            } catch (NoSuchMethodException e) {
                URI sourceUri = wire.getSourceUri();
                URI targetUri = wire.getTargetUri();
                throw new WireAttachException("No matching method found", sourceUri, targetUri, e);
            }
            chain.addInterceptor(new JavaInvokerInterceptor(method, component, scopeContainer));
        }
    }

}
