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
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.osoa.sca.annotations.Property;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class PropertyAnnotationProcessor implements BeanPostProcessor {

    private Class<? extends Annotation> propertyAnnotationType = Property.class;
    
    private RuntimeComponent component;
    
    private JavaPropertyValueObjectFactory propertyFactory;
    
    public PropertyAnnotationProcessor (JavaPropertyValueObjectFactory propertyFactory, 
                                        RuntimeComponent component) {
        this.propertyFactory = propertyFactory;
        this.component = component;
    }

    /**
     * Gets property annotation type.
     */
    protected Class<? extends Annotation> getPropertyAnnotationType() {
        return this.propertyAnnotationType;
    }

    /**
     * Sets property annotation type.
     */
    public void setPropertyAnnotationType(Class<? extends Annotation> propertyAnnotationType) {
        Assert.notNull(propertyAnnotationType, "'propertyAnnotationType' type must not be null.");
        this.propertyAnnotationType = propertyAnnotationType;
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
     * <p>Processes a beans fields for injection if it has a {@link Property} annotation.</p>
     */
    protected void processAnnotation(final Object bean) {
        
        final Class<?> clazz = bean.getClass();       

        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                //Annotation annotation = method.getAnnotation(getPropertyAnnotationType());                
                Property annotation = (Property) method.getAnnotation(getPropertyAnnotationType());
                
                if (annotation != null) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Property annotation is not supported on static methods");
                    }
                    
                    if (Modifier.isPrivate(method.getModifiers())) {
                        throw new IllegalStateException("Property annotation is not supported on private methods");
                    }

                    if (method.getParameterTypes().length == 0) {
                        throw new IllegalStateException("Property annotation requires at least one argument: " + method);
                    }
                    
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                    if (pd != null) {
                        String propName = annotation.name();
                        if ("".equals(propName)) {
                            injectProperty(bean, pd, getPropertyObj(pd.getPropertyType(), pd.getName()));
                        } else {
                            injectProperty(bean, pd, getPropertyObj(pd.getPropertyType(), propName));
                        }
                    }
                }
            }
        });
        
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) {
                //Annotation annotation = field.getAnnotation(getPropertyAnnotationType());
                Property annotation = (Property) field.getAnnotation(getPropertyAnnotationType());
                
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("Property annotation is not supported on static fields");
                    }
                    
                    if (Modifier.isPrivate(field.getModifiers())) {
                        throw new IllegalStateException("Property annotation is not supported on private fields");
                    }

                    ReflectionUtils.makeAccessible(field);
                    
                    Object propertyObj = null;
                    String propName = annotation.name();
                    if ("".equals(propName)) {
                        propertyObj = getPropertyObj(field.getType(), field.getName());                        
                    } else {
                        propertyObj = getPropertyObj(field.getType(), propName);
                    }
                    
                    if (propertyObj != null)
                        ReflectionUtils.setField(field, bean, propertyObj);
                }
            }
        });
    }
    
    /**
     * Processes a property descriptor to inject a service.
     */
    public Object getPropertyObj(Class requiredType, String name) {
        
        Object propertyObj = null;
        
        List<ComponentProperty> props = component.getProperties();
        for (ComponentProperty prop : props) {
            if (prop.getName().equals(name)) {
                // On finding the property, create a factory for it and create a Bean using
                // the factory
                ObjectFactory factory = propertyFactory.createValueFactory(prop, prop.getValue(), requiredType);
                propertyObj = factory.getInstance();
            } // end if
        } // end for        
        
        return propertyObj;
    }
    
    
    public void injectProperty(Object bean, PropertyDescriptor pd, Object propertyObj) {
        
        if (propertyObj != null) {
            try {                                                       
                pd.getWriteMethod().invoke(bean, new Object[] { propertyObj });
            } catch (Throwable e) {
                throw new FatalBeanException("Problem injecting property:  " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Processes a property descriptor to inject a service.
     */
    /*public void injectMethod(Object bean, Method method) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);        

        if (pd != null) {
            Object propertyObj = null;
            
            List<ComponentProperty> props = component.getProperties();
            for (ComponentProperty prop : props) {
                if (prop.getName().equals(pd.getName())) {
                    // On finding the property, create a factory for it and create a Bean using
                    // the factory
                    ObjectFactory factory = propertyFactory.createValueFactory(prop, prop.getValue(), pd.getPropertyType());
                    propertyObj = factory.getInstance();
                } // end if
            } // end for

            if (propertyObj != null) {
                try {                                                       
                    pd.getWriteMethod().invoke(bean, new Object[] { propertyObj });
                } catch (Throwable e) {
                    throw new FatalBeanException("Problem injecting property:  " + e.getMessage(), e);
                }
            }
        }
    }*/
}
