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
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

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
    private ProxyService proxyService;
    private ClassLoader loader;

    /**
     * Creates a new composite
     *
     * @param uri            the uri of the SCA composite
     * @param resource       a resource pointing to the application context
     * @param propertyValues the values of this composite's Properties
     */
    public SpringCompositeComponent(URI uri,
                                    Resource resource,
                                    ProxyService proxyService,
                                    Map<String, Document> propertyValues,
                                    ClassLoader loader) {
        super(uri);
        this.resource = resource;
        this.proxyService = proxyService;
        this.loader = loader;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        TargetInvoker invoker = super.createTargetInvoker(targetName, operation);
        if (invoker != null) {
            return invoker;
        }
        // no service found, wire to a bean using the service name as the bean name
        ServiceContract contract = operation.getServiceContract();
        Method method;
        try {
            method = findMethod(contract.getInterfaceClass(), operation);
        } catch (NoSuchMethodException e) {
            throw new BeanMethodNotFound(operation);
        }
        return new SpringInvoker(targetName, method, this);
    }

    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
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
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                // FIXME this is horrible
                Thread.currentThread().setContextClassLoader(loader);
                springContext = new SCAApplicationContext(scaApplicationContext, resource);
                springContext.start();
            } finally {
                Thread.currentThread().setContextClassLoader(cl);

            }
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
     * Used in unit testing
     */
    void setSpringContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
    }

    /**
     * TODO remove need for inner class as SCA.getParent() has been removed and no longer clashes with
     * ApplicaitonContext.getParent
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
            Class<?> type = null;
            if (object instanceof Reference) {
                Reference reference = (Reference) object;
                Wire wire = null;
                if (!reference.getReferenceBindings().isEmpty()) {
                    // FIXME JFM provide a better way for the runtime to select the binding as opposed to the first one
                    wire = reference.getReferenceBindings().get(0).getWire();
                    type = wire.getSourceContract().getInterfaceClass();
                }
                if (requiredType != null && requiredType.isAssignableFrom(type)) {
                    // need null check since Spring may pass in a null
                    throw new BeanNotOfRequiredTypeException(name, requiredType, type);
                }
                return proxyService.createProxy(type, wire);
            } else if (object instanceof ServiceBinding) {
                ServiceBinding serviceBinding = (ServiceBinding) object;
                type = serviceBinding.getWire().getSourceContract().getInterfaceClass();
                if (requiredType != null && requiredType.isAssignableFrom(type)) {
                    // need null check since Spring may pass in a null
                    throw new BeanNotOfRequiredTypeException(name, requiredType, type);
                }
                return proxyService.createProxy(type, serviceBinding.getWire());
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
            return getUri().toString();
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
