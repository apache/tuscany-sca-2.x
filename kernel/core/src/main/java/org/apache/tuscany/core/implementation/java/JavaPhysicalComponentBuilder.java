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

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.core.implementation.POJOPhysicalComponentBuilder;
import org.apache.tuscany.core.injection.CallbackWireObjectFactory2;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import static org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType.CALLBACK;
import static org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType.REFERENCE;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;
import org.apache.tuscany.core.wire.WireObjectFactory2;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.builder.physical.WireAttachException;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
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
@EagerInit
@Service(interfaces={PhysicalComponentBuilder.class, WireAttacher.class})
public class JavaPhysicalComponentBuilder<T>
    extends POJOPhysicalComponentBuilder<JavaPhysicalComponentDefinition<T>, JavaComponent<T>>
    implements WireAttacher<JavaComponent, JavaPhysicalWireSourceDefinition, JavaPhysicalWireTargetDefinition> {

    private ProxyService proxyService;

    public JavaPhysicalComponentBuilder(
        @Reference(name = "builderRegistry")PhysicalComponentBuilderRegistry builderRegistry,
        @Reference(name = "scopeRegistry")ScopeRegistry scopeRegistry,
        @Reference(name = "providerBuilders")IFProviderBuilderRegistry providerBuilders,
        @Reference(name = "classloaderRegistry")ClassLoaderRegistry classLoaderRegistry) {
        super(builderRegistry, scopeRegistry, providerBuilders, classLoaderRegistry);
    }

    @Reference
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public JavaComponent<T> build(JavaPhysicalComponentDefinition<T> definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        URI groupId = definition.getGroupId();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(definition.getClassLoaderId());

        // get the scope container for this component
        Scope scope = definition.getScope();
        ScopeContainer<?> scopeContainer = scopeRegistry.getScopeContainer(scope);

        // create the InstanceFactoryProvider based on the definition in the model
        InstanceFactoryProviderDefinition<T> providerDefinition = definition.getInstanceFactoryProviderDefinition();
        InstanceFactoryProvider<T> provider = providerBuilders.build(providerDefinition, classLoader);

        return new JavaComponent<T>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
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
    public void attachToSource(JavaComponent source,
                       Component target,
                       Wire wire,
                       JavaPhysicalWireSourceDefinition definition) {
        URI sourceUri = definition.getUri();
        InjectionSource referenceSource = new InjectionSource(REFERENCE, sourceUri.getFragment());
        Class<?> type = source.getMemberType(referenceSource);
        if (definition.isOptimizable()) {
            assert target instanceof AtomicComponent;
            ObjectFactory<?> factory = ((AtomicComponent<?>)target).createObjectFactory();
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
     * @param source
     * @param component Component.
     * @param wire      the wire to attach
     * @param target    Target.
     */
    public void attachToTarget(Component source, JavaComponent component, Wire wire, JavaPhysicalWireTargetDefinition target)
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
