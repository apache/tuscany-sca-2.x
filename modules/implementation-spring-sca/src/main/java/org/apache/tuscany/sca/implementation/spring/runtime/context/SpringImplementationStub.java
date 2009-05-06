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

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * This is the runtime side stub for the corresponding Tuscany-side stub class.
 * It enables the Spring code in the runtime module to invoke methods on a
 * Tuscany SpringImplementation without the Spring runtime module
 * needing to know about any Tuscany classes. See the SpringImplementationTie class
 * in the implementation-spring module for what the tie does. 
 */
public class SpringImplementationStub {

    Object tie;
    Method getURI;
    Method getBean;
    Method getComponentName;
    Method getComponentTie;
    Method getPropertyValueTie;
    
    public SpringImplementationStub(Object tie) {
        this.tie = tie;
        Class<?> tieClass = tie.getClass();
        try {
            getURI = tieClass.getMethod("getURI", new Class<?>[]{});
            getBean = tieClass.getMethod("getBean", new Class<?>[]{String.class, Class.class});
            getComponentName = tieClass.getMethod("getComponentName");
            getComponentTie = tieClass.getMethod("getComponentTie");
            getPropertyValueTie = tieClass.getMethod("getPropertyValueTie");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getURI() {
        try {

            return (String)getURI.invoke(tie);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a Bean for a reference or for a property.
     *
     * @param name - the name of the Bean required
     * @param requiredType - the required type of the Bean (either a Java class or a Java interface)
     * @return Object - a Bean which matches the requested bean
     */
    public Object getBean(String name, Class<?> requiredType) throws BeansException {
        try {

            Object bean = getBean.invoke(tie, new Object[] {name, requiredType});
            if (bean == null) {
                throw new NoSuchBeanDefinitionException("Unable to find Bean with name " + name);
            }
            return bean;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getComponentName() {
        try {

            return (String)getComponentName.invoke(tie);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getComponentTie() {
        try {

            return getComponentTie.invoke(tie);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getPropertyValueTie() {
        try {

            return getPropertyValueTie.invoke(tie);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
