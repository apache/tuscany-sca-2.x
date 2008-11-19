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

import org.osoa.sca.annotations.Reference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class ReferenceAnnotationProcessor implements BeanPostProcessor {

    private Class<? extends Annotation> referenceAnnotationType = Reference.class;
    
    private RuntimeComponent component;
    
    public ReferenceAnnotationProcessor (RuntimeComponent component) {
        this.component = component;
    }
    
    /**
     * Gets referece annotation type.
     */
    protected Class<? extends Annotation> getReferenceAnnotationType() {
        return this.referenceAnnotationType;
    }

    /**
     * Sets referece annotation type.
     */
    public void setReferenceAnnotationType(Class<? extends Annotation> referenceAnnotationType) {
        Assert.notNull(referenceAnnotationType, "'referenceAnnotationType' type must not be null.");
        this.referenceAnnotationType = referenceAnnotationType;
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

        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                //Annotation annotation = method.getAnnotation(getReferenceAnnotationType());                
                Reference annotation = (Reference) method.getAnnotation(getReferenceAnnotationType());
                
                if (annotation != null) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Reference annotation is not supported on static methods");
                    }
                    
                    if (Modifier.isPrivate(method.getModifiers())) {
                        throw new IllegalStateException("Reference annotation is not supported on private methods");
                    }

                    if (method.getParameterTypes().length == 0) {
                        throw new IllegalStateException("Reference annotation requires at least one argument: " + method);
                    }
                    
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                    if (pd != null) {
                        String refName = annotation.name();
                        if ("".equals(refName)) {
                            injectReference(bean, pd, pd.getName());
                        } else {
                            injectReference(bean, pd, refName);
                        }
                    }
                }
            }
        });
        
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) {
                //Annotation annotation = field.getAnnotation(getReferenceAnnotationType());                
                Reference annotation = (Reference) field.getAnnotation(getReferenceAnnotationType());
                
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("Reference annotation is not supported on static fields");
                    }
                    
                    if (Modifier.isPrivate(field.getModifiers())) {
                        throw new IllegalStateException("Reference annotation is not supported on private fields");
                    }

                    ReflectionUtils.makeAccessible(field);
                    
                    Object referenceObj = null;
                    String refName = annotation.name();
                    if ("".equals(refName)) {
                        referenceObj = component.getComponentContext().getService(field.getType(), field.getName());
                    } else {
                        referenceObj = component.getComponentContext().getService(field.getType(), refName);
                    }                        
                    
                    if (referenceObj != null)
                        ReflectionUtils.setField(field, bean, referenceObj);
                }
            }
        });
    }
    
    /**
     * Processes a property descriptor to inject a service.
     */
    public void injectReference(Object bean, PropertyDescriptor pd, String name) {
               
        Object referenceObj = component.getComponentContext().getService(pd.getPropertyType(), name);
        
        if (referenceObj != null) {
            try {                                                       
                pd.getWriteMethod().invoke(bean, new Object[] { referenceObj });
            } catch (Throwable e) {
                throw new FatalBeanException("Problem injecting reference:  " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Processes a property descriptor to inject a service.
     */
    /*public void injectMethod(Object bean, Method method) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);        

        if (pd != null) {            
            Object referenceObj = component.getComponentContext().getService(pd.getPropertyType(), pd.getName());

            if (referenceObj != null) {
                try {                                                       
                    pd.getWriteMethod().invoke(bean, new Object[] { referenceObj });
                } catch (Throwable e) {
                    throw new FatalBeanException("Problem injecting reference:  " + e.getMessage(), e);
                }
            }
        }
    }*/
}
