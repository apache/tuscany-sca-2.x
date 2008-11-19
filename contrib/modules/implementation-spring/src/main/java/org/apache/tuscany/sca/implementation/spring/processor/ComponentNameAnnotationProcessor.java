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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

import org.springframework.util.Assert;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.osoa.sca.annotations.ComponentName;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class ComponentNameAnnotationProcessor implements BeanPostProcessor {

    private Class<? extends Annotation> componentNameAnnotationType = ComponentName.class;
    
    private RuntimeComponent component;
    
    public ComponentNameAnnotationProcessor (RuntimeComponent component) {
        this.component = component;
    }
    
    /**
     * Gets componentName annotation type.
     */
    protected Class<? extends Annotation> getComponentNameAnnotationType() {
        return this.componentNameAnnotationType;
    }

    /**
     * Sets componentName annotation type.
     */
    public void setComponentNameAnnotationType(Class<? extends Annotation> componentNameAnnotationType) {
        Assert.notNull(componentNameAnnotationType, "'componentNameAnnotationType' type must not be null.");
        this.componentNameAnnotationType = componentNameAnnotationType;
    }

    /**
     * This method is used to execute before a bean's initialization callback.
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) 
    throws BeansException {
        processAnnotation(bean);
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

    /**
     * <p>Processes a beans fields for injection if it has a {@link Reference} annotation.</p>
     */
    protected void processAnnotation(final Object bean) {
        
        final Class<?> clazz = bean.getClass();

        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) {
                Annotation annotation = field.getAnnotation(getComponentNameAnnotationType());

                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("ComponentName annotation is not supported on static fields");
                    }
                    
                    if (Modifier.isPrivate(field.getModifiers())) {
                        throw new IllegalStateException("ComponentName annotation is not supported on private fields");
                    }

                    ReflectionUtils.makeAccessible(field);
                    
                    if (field.getType().getName().equals("java.lang.String")) {
                        Object nameObj = component.getName();   
                        if (nameObj != null)
                            ReflectionUtils.setField(field, bean, nameObj);
                    } else {
                        throw new IllegalStateException("ComponentName annotation is supported only on java.lang.String field type.");
                    }
                }
            }
        });

        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                Annotation annotation = method.getAnnotation(getComponentNameAnnotationType());
                
                if (annotation != null) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("ComponentName annotation is not supported on static methods");
                    }
                    
                    if (Modifier.isPrivate(method.getModifiers())) {
                        throw new IllegalStateException("ComponentName annotation is not supported on private methods");
                    }

                    if (method.getParameterTypes().length == 0) {
                        throw new IllegalStateException("ComponentName annotation requires at least one argument: " + method);
                    }
                    
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);                    
                    
                    if (pd.getPropertyType().getName().equals("java.lang.String")) {
                        Object nameObj = component.getName();                    
                        if (nameObj != null) {
                            try {                                                       
                                pd.getWriteMethod().invoke(bean, new Object[] { nameObj });
                            } catch (Throwable e) {
                                throw new FatalBeanException("Problem injecting reference:  " + e.getMessage(), e);
                            }
                        }
                    } else {
                        throw new IllegalStateException("ComponentName annotation is supported only on java.lang.String field type.");
                    }
                }
            }
        });
    }
}
