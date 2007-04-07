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

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.core.implementation.PojoComponentContextFactory;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.ResourceObjectFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Resource;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.host.ResourceHost;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Reference;

/**
 * Builds a Java-based atomic context from a component definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    private ResourceHost host;

    @Reference
    public void setHost(ResourceHost host) {
        this.host = host;
    }

    @SuppressWarnings("unchecked")
    public AtomicComponent build(Component definition, DeploymentContext context)
        throws BuilderConfigException {
        JavaImplementationDefinition componentType = (JavaImplementationDefinition)
            definition.getImplementation();

        PojoConfiguration configuration = new PojoConfiguration();
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

        configuration.setGroupId(context.getGroupId());
        configuration.setProxyService(proxyService);
        configuration.setWorkContext(workContext);
        configuration.setImplementationClass(definition.getImplementation().getImplementationClass());

        // setup property injection sites
        for (JavaElement property : componentType.getPropertyMembers().values()) {
            configuration.addPropertySite(property.getName(), property);
        }

        // setup reference injection sites
        for (JavaElement reference : componentType.getReferenceMembers().values()) {
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
        configuration.setConstructor(ctorDef);

        configuration.setName(definition.getUri());
        handleCallbackSites(componentType, configuration);

        JavaAtomicComponent component = new JavaAtomicComponent(configuration);

        // handle properties
        handleProperties(definition, component);

        // handle resources
        handleResources(componentType, component);

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
            String name = service.getServiceContract().getCallbackName();
            if (name != null) {
                // Only if there is a callback reference in the service
                configuration.addCallbackSite(name, service.getCallbackMember());
            }
        }
    }

    private void handleResources(
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType,
        JavaAtomicComponent component) {
        for (Resource<?> resource : componentType.getResources().values()) {
            String name = resource.getName();
            ObjectFactory<?> objectFactory = resource.getObjectFactory();
            if (objectFactory == null) {
                Class<?> type = resource.getType();
                if (ComponentContext.class.equals(type)) {
                    objectFactory = new PojoComponentContextFactory(component);
                } else {
                    boolean optional = resource.isOptional();
                    String mappedName = resource.getMappedName();
                    objectFactory = createResourceObjectFactory(type, mappedName, optional, host);
                }
            }
            component.addResourceFactory(name, objectFactory);
        }
    }

    private <T> ResourceObjectFactory<T> createResourceObjectFactory(Class<T> type,
                                                                     String mappedName,
                                                                     boolean optional,
                                                                     ResourceHost host) {
        return new ResourceObjectFactory<T>(type, mappedName, optional, host);
    }

    private void handleProperties(Component definition, JavaAtomicComponent component) {
        for (PropertyValue<?> property : definition.getPropertyValues().values()) {
            ObjectFactory<?> factory = property.getValueFactory();
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }
    }

    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }

}
