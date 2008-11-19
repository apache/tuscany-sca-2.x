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
package org.apache.tuscany.sca.implementation.spring.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class ConstructorAnnotationProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    private Class<? extends Annotation> constructorAnnotationType 
                                            = org.osoa.sca.annotations.Constructor.class;
    
    private Class<? extends Annotation> autowiredAnnotationType = Autowired.class;
    
    public ConstructorAnnotationProcessor () {
        // Default constructor.
    }
    
    /**
     * Set the 'autowired' annotation type, to be used on constructors, fields,
     * setter methods and arbitrary config methods.
     */
    public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
        Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
        this.autowiredAnnotationType = autowiredAnnotationType;
    }

    /**
     * Return the 'autowired' annotation type.
     */
    protected Class<? extends Annotation> getAutowiredAnnotationType() {
        return this.autowiredAnnotationType;
    }
    
    /**
     * Return the 'constructor' annotation type.
     */
    protected Class<? extends Annotation> getConstructorAnnotationType() {
        return this.constructorAnnotationType;
    }

    /**
     * Sets the 'constructor' annotation type.
     */
    public void setConstructorAnnotationType(Class<? extends Annotation> constructorAnnotationType) {
        Assert.notNull(constructorAnnotationType, "'constructorAnnotationType' type must not be null.");
        this.constructorAnnotationType = constructorAnnotationType;
    }

    /**
     * This method is used to execute before a bean's initialization callback.
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) 
    throws BeansException {
        return bean;
    }

    /**
     * This method is used to execute after a bean's initialization callback.
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessAfterInitialization(Object bean, String beanName)
    throws BeansException {
        return bean;
    }
    
    public Constructor[] determineCandidateConstructors(Class beanClass, String beanName) throws BeansException {
        /*Constructor[] declaredConstructors = beanClass.getDeclaredConstructors();                
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        List candidates = new ArrayList(declaredConstructors.length);

        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            Annotation annotation = method.getAnnotation(getConstructorAnnotationType());
            if (annotation != null) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException("Constructor annotation is not supported on static methods");
                }
                
                if (candidates.size() == 1) {
                    throw new IllegalStateException("Only one method is allowed to have constructor annotation in a bean: " + method);
                }
                
                candidates.add(method);
            }
        }

        return (Constructor[]) candidates.toArray(new Constructor[candidates.size()]);*/
        return null;
    }
}
