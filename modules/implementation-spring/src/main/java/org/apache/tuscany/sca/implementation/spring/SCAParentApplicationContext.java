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
package org.apache.tuscany.sca.implementation.spring;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

/**
 * A Spring ParentApplicationContext for a given Spring Implementation
 *
 * The Parent application context is responsible for handling those entities within a Spring
 * application context that actually belong to SCA rather than to Spring.  The principal things
 * are Properties and References.  These may be present either through explicit <sca:property/>
 * and <sca:reference/> elements in the application context or they may be implicit through
 * unresolved Spring bean <property.../> elements.  In either case, it is the Parent application
 * context that must provide Spring beans that correspond to the property or reference, as derived
 * from the SCA composite in which the Spring application context is an implementation.
 *
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $
 */
class SCAParentApplicationContext implements ApplicationContext {

    // The Spring implementation for which this is the parent application context
    private SpringImplementation implementation;
    private RuntimeComponent component;
    private JavaPropertyValueObjectFactory propertyFactory;

    private static final String[] EMPTY_ARRAY = new String[0];

    public SCAParentApplicationContext(RuntimeComponent component,
                                       SpringImplementation implementation,
                                       ProxyFactory proxyService,
                                       JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        this.implementation = implementation;
        this.component = component;
        this.propertyFactory = propertyValueObjectFactory;
    } // end constructor

    public Object getBean(String name) throws BeansException {
        return getBean(name, null);
    }

    /**
     * Get a Bean for a reference or for a property..
     * @param name - the name of the Bean required
     * @param requiredtype - the required type of the Bean (either a Java class or a Java interface)
     * @return Object - a Bean which matches the requested bean
     */
    public Object getBean(String name, Class requiredType) throws BeansException {
        System.out.println("Spring parent context - getBean called for name: " + name);
        // The expectation is that the requested Bean is either a reference or a property
        // from the Spring context
        for (Reference reference : implementation.getReferences()) {
            if (reference.getName().equals(name)) {
                // Extract the Java interface for the reference (it can't be any other interface type
                // for a Spring application context)
                if (requiredType == null) {
                    JavaInterface javaInterface = (JavaInterface)reference.getInterfaceContract().getInterface();
                    requiredType = javaInterface.getJavaClass();
                }
                // Create and return eturn the proxy for the reference
                return getService(requiredType, reference.getName());
            } // end if
        } // end for

        // For a property, get the name and the required Java type and create a Bean
        // of that type with the value inserted.
        for (Property property : implementation.getProperties()) {
            if (property.getName().equals(name)) {
                if (requiredType == null) {
                    // The following code only deals with a subset of types and was superceded
                    // by the information from the implementation (which uses Classes as found
                    // in the Spring implementation itself.
                    //requiredType = JavaXMLMapper.getJavaType( property.getXSDType() );
                    requiredType = implementation.getPropertyClass(name);
                }
                return getPropertyBean(requiredType, property.getName());
            } // end if
        } // end for
        throw new NoSuchBeanDefinitionException("Unable to find Bean with name " + name);

    } // end method getBean( String, Class )

    /**
     * Creates a proxy Bean for a reference
     * @param <B> the Business interface type for the reference
     * @param businessInterface - the business interface as a Class
     * @param referenceName - the name of the Reference
     * @return an Bean of the type defined by <B>
     */
    private <B> B getService(Class<B> businessInterface, String referenceName) {
        return component.getComponentContext().getService(businessInterface, referenceName);
    }

    /**
     * Method to create a Java Bean for a Property value
     * @param <B> the class type of the Bean
     * @param requiredType - a Class object for the required type
     * @param name - the Property name
     * @return - a Bean of the specified property, with value set
     */
    private <B> B getPropertyBean(Class requiredType, String name) {
        B propertyObject = null;
        // Get the component's list of properties
        List<ComponentProperty> props = component.getProperties();
        for (ComponentProperty prop : props) {
            if (prop.getName().equals(name)) {
                // On finding the property, create a factory for it and create a Bean using
                // the factory
                ObjectFactory factory = propertyFactory.createValueFactory(prop, prop.getValue(), requiredType);
                propertyObject = (B)factory.getInstance();
            } // end if
        } // end for

        return propertyObject;
    }

    public boolean containsBean(String name) {
        // TODO
        System.out.println("Spring parent context - containsBean called for name: " + name);
        return false;
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        // TODO
        return false;
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
        return implementation.getURI();
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

    public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        return null;
    }

    public boolean isPrototype(String theString) {
        return false;
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
