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
package org.apache.tuscany.container.spring.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.container.spring.context.SCAApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;

/**
 * A composite implementation responsible for managing Spring application contexts.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeComponent extends CompositeComponentExtension {
    private static final String[] EMPTY_ARRAY = new String[0];
    private AbstractApplicationContext springContext;
    private Resource resource;
    private WireService wireService;

    /**
     * Creates a new composite
     *
     * @param name           the name of the SCA composite
     * @param resource       a resource pointing to the application context
     * @param parent         the SCA composite parent
     * @param wireService    the wire service to create proxies
     * @param connector      the connector to use for wiring children
     * @param propertyValues the values of this composite's Properties
     */
    public SpringCompositeComponent(String name,
                                    Resource resource,
                                    CompositeComponent parent,
                                    WireService wireService,
                                    Connector connector,
                                    Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
        this.resource = resource;
        this.wireService = wireService;
    }

    /**
     * Creates a new composite
     *
     * @param name           the name of the SCA composite
     * @param context        the Spring application context
     * @param parent         the SCA composite parent
     * @param connector      the connector to use for wiring children
     * @param propertyValues the values of this composite's Properties
     */
    public SpringCompositeComponent(String name,
                                    AbstractApplicationContext context,
                                    CompositeComponent parent,
                                    Connector connector,
                                    Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
        this.springContext = context;
        SCAParentApplicationContext scaApplicationContext = new SCAParentApplicationContext();
        springContext.setParent(scaApplicationContext);
        // REVIEW we need to refresh to pick up the parent but this is not optimal
        springContext.refresh();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        ServiceContract contract = operation.getServiceContract();
        Method[] methods = contract.getInterfaceClass().getMethods();
        Method method = findMethod(operation, methods);
        // FIXME test m == null
        // Treat the serviceName as the Spring bean name to look up
        return new SpringInvoker(targetName, method, this);
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        // not needed
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return springContext;
    }

    public void prepare() {
        // TODO handle only references with a composite binding
    }

    public void start() {
        super.start();
        for (SCAObject child : children.values()) {
            child.start();
        }
        if (springContext == null) {
            SCAParentApplicationContext scaApplicationContext = new SCAParentApplicationContext();
            springContext = new SCAApplicationContext(scaApplicationContext, resource);
            springContext.start();
        }
    }

    public void stop() {
        super.stop();
        springContext.stop();
    }

    public <T> T getBean(Class<T> serviceInterface, String name) {
        return serviceInterface.cast(springContext.getBean(name));
    }

    /**
     * An inner class is required to act as the Spring application context parent as opposed to implementing the
     * interface since the return types for {@link org.springframework.context.ApplicationContext#getParent()} and
     * {@link org.apache.tuscany.spi.component.CompositeComponent#getParent()} clash
     */
    private class SCAParentApplicationContext implements ApplicationContext {

        public Object getBean(String name) throws BeansException {
            return getBean(name, null);
        }

        @SuppressWarnings("unchecked")
        public Object getBean(String name, Class requiredType) throws BeansException {
            SCAObject object = children.get(name);   // keep cast due to compiler error
            if (object == null) {
                return null;
            }
            Class<?> type;
            if (object instanceof Reference) {
                Reference reference = (Reference) object;
                type = reference.getInboundWire().getServiceContract().getInterfaceClass();
                if (requiredType != null && requiredType.isAssignableFrom(type)) {
                    // need null check since Spring may pass in a null
                    throw new BeanNotOfRequiredTypeException(name, requiredType, type);
                }
                return wireService.createProxy(type, reference.getInboundWire());
            } else if (object instanceof ServiceBinding) {
                ServiceBinding serviceBinding = (ServiceBinding) object;
                type = serviceBinding.getInboundWire().getServiceContract().getInterfaceClass();
                if (requiredType != null && requiredType.isAssignableFrom(type)) {
                    // need null check since Spring may pass in a null
                    throw new BeanNotOfRequiredTypeException(name, requiredType, type);
                }
                return wireService.createProxy(type, serviceBinding.getInboundWire());
            } else {
                throw new AssertionError("Illegal object type [" + name + "]");
            }
        }

        public boolean containsBean(String name) {
            return children.get(name) != null;
        }

        public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
            return children.get(name) != null;
        }

        public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
            throw new UnsupportedOperationException();
        }

        public Class getType(String name) throws NoSuchBeanDefinitionException {
            return null;
        }

        public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
            return EMPTY_ARRAY;
        }

        public ApplicationContext getParent() {
            return null;
        }

        public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
            return null;
        }

        public String getDisplayName() {
            return getName();
        }

        public long getStartupDate() {
            return 0;
        }

        public boolean containsBeanDefinition(String beanName) {
            return false;
        }

        public int getBeanDefinitionCount() {
            return 0;
        }

        public String[] getBeanDefinitionNames() {
            return new String[0];
        }

        public String[] getBeanNamesForType(Class type) {
            return new String[0];
        }

        public String[] getBeanNamesForType(Class type, boolean includePrototypes, boolean includeFactoryBeans) {
            return new String[0];
        }

        public Map getBeansOfType(Class type) throws BeansException {
            return null;
        }

        public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans)
            throws BeansException {
            return null;
        }

        public BeanFactory getParentBeanFactory() {
            return null;
        }

        public boolean containsLocalBean(String name) {
            return false;
        }

        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return null;
        }

        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            return null;
        }

        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            return null;
        }

        public void publishEvent(ApplicationEvent event) {

        }

        public Resource[] getResources(String locationPattern) throws IOException {
            return new Resource[0];
        }

        public Resource getResource(String location) {
            return null;
        }

        public ClassLoader getClassLoader() {
            // REVIEW: this is almost certainly flawed, but it's not clear how the SCA runtime's
            // resource loading mechanism is exposed right now.
            return this.getClass().getClassLoader();
        }
    }
}
