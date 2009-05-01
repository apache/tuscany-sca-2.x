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
package org.apache.tuscany.sca.implementation.spring.runtime.context;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

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
    private SpringImplementationStub implementation;

    private static final String[] EMPTY_ARRAY = new String[0];

    public SCAParentApplicationContext(SpringImplementationStub implementation) {
        this.implementation = implementation;
    } // end constructor

    public Object getBean(String name) throws BeansException {
        return getBean(name, (Class) null);
    }

    /**
     * Get a Bean for a reference or for a property.
     *
     * @param name - the name of the Bean required
     * @param requiredType - the required type of the Bean (either a Java class or a Java interface)
     * @return Object - a Bean which matches the requested bean
     */
    public Object getBean(String name, Class requiredType) throws BeansException {
        return implementation.getBean(name, requiredType);
    } // end method getBean( String, Class )

    public Object getBean(String name, Object[] args) throws BeansException {
         return getBean(name, ((Class)null));
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

    public String getId() {
          return this.toString();
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
