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
package org.apache.tuscany.runtime.standalone.host.implementation.launched;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.ResourceObjectFactory;

/**
 * @version $Revsion$ $Date$
 *          <p/>
 *          TODO This is a straight copy from the JUnit component builder
 */
public class LaunchedComponentBuilder extends ComponentBuilderExtension<Launched> {

    private ResourceHost host;

    @Reference
    public void setHost(ResourceHost host) {
        this.host = host;
    }

    @Override
    protected Class<Launched> getImplementationType() {
        return Launched.class;
    }

    @SuppressWarnings({"unchecked"})
    public Component build(ComponentDefinition<Launched> definition, DeploymentContext deployment)
        throws BuilderConfigException {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
            definition.getImplementation().getComponentType();
        Class<?> implClass = componentType.getImplClass();

        PojoConfiguration configuration = new PojoConfiguration();
        if (definition.getInitLevel() != null) {
            configuration.setInitLevel(definition.getInitLevel());
        } else {
            configuration.setInitLevel(componentType.getInitLevel());
        }
        if (componentType.getMaxAge() > 0) {
            configuration.setMaxAge(componentType.getMaxAge());
        } else if (componentType.getMaxIdleTime() > 0) {
            configuration.setMaxIdleTime(componentType.getMaxIdleTime());
        }
        Method initMethod = componentType.getInitMethod();
        if (initMethod != null) {
            configuration.setInitInvoker(new MethodEventInvoker(initMethod));
        }
        Method destroyMethod = componentType.getDestroyMethod();
        if (destroyMethod != null) {
            configuration.setDestroyInvoker(new MethodEventInvoker(destroyMethod));
        }

        configuration.setProxyService(proxyService);
        configuration.setWorkContext(workContext);
        configuration.setImplementationClass(implClass);
        configuration.setGroupId(deployment.getGroupId());

        // setup property injection sites
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            configuration.addPropertySite(property.getName(), property.getMember());
        }

        // setup reference injection sites
        for (JavaMappedReference reference : componentType.getReferences().values()) {
            Member member = reference.getMember();
            if (member != null) {
                // could be null if the reference is mapped to a constructor
                configuration.addReferenceSite(reference.getUri().getFragment(), member);
            }
        }

        for (Resource resource : componentType.getResources().values()) {
            Member member = resource.getMember();
            if (member != null) {
                // could be null if the resource is mapped to a constructor
                configuration.addResourceSite(resource.getName(), member);
            }
        }

        // setup constructor injection
        ConstructorDefinition<?> ctorDef = componentType.getConstructorDefinition();
        Constructor<?> constr = ctorDef.getConstructor();
        PojoObjectFactory<?> instanceFactory = new PojoObjectFactory(constr);
        configuration.setInstanceFactory(instanceFactory);
        for (Parameter param : ctorDef.getParameters()) {
            configuration.getConstructorParamNames().add(param.getName());
            configuration.addConstructorParamType(param.getType());
        }
        configuration.setName(definition.getUri());
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);

        // handle properties
        handleProperties(definition, component);

        // handle resources
        handleResources(componentType, component);

        handleCallbackSites(componentType, configuration);

        if (componentType.getConversationIDMember() != null) {
            component.addConversationIDFactory(componentType.getConversationIDMember());
        }

        return component;
    }

    private void handleCallbackSites(
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType,
        PojoConfiguration configuration) {
        for (JavaMappedService service : componentType.getServices().values()) {
            // setup callback injection sites
            if (service.getCallbackReferenceName() != null) {
                // Only if there is a callback reference in the service
                configuration.addCallbackSite(service.getCallbackReferenceName(), service.getCallbackMember());
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void handleResources(
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType,
        JavaAtomicComponent component) {

        for (Resource resource : componentType.getResources().values()) {
            ObjectFactory<?> objectFactory = resource.getObjectFactory();
            if (objectFactory != null) {
                component.addResourceFactory(resource.getName(), objectFactory);
            } else {
                String name = resource.getName();
                boolean optional = resource.isOptional();
                Class<Object> type = (Class<Object>) resource.getType();
                ResourceObjectFactory<Object> factory;
                String mappedName = resource.getMappedName();
                if (mappedName == null) {
                    // by type
                    factory = new ResourceObjectFactory<Object>(type, optional, host);
                } else {
                    factory = new ResourceObjectFactory<Object>(type, mappedName, optional, host);
                }
                component.addResourceFactory(name, factory);
            }
        }
    }

    private void handleProperties(ComponentDefinition<Launched> definition, JavaAtomicComponent component) {
        for (PropertyValue<?> property : definition.getPropertyValues().values()) {
            ObjectFactory<?> factory = property.getValueFactory();
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }
    }

}
