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
package org.apache.tuscany.sca.implementation.spring.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringConstructorArgElement;
import org.apache.tuscany.sca.implementation.spring.SpringPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class SCAGenericApplicationContext extends GenericApplicationContext {

    private ClassLoader classloader = null;
    private List<SpringSCAPropertyElement> propertyElements = new ArrayList<SpringSCAPropertyElement>();
    private List<SpringSCAServiceElement> serviceElements = new ArrayList<SpringSCAServiceElement>();
    private List<SpringSCAReferenceElement> referenceElements = new ArrayList<SpringSCAReferenceElement>();
    private List<SpringBeanElement> beanElements;

    public SCAGenericApplicationContext(DefaultListableBeanFactory beanFactory,
                                        ApplicationContext parent,
                                        ClassLoader classloader) {
        super(beanFactory, parent);
        this.classloader = classloader;
    }

    public SCAGenericApplicationContext(ApplicationContext parent, ClassLoader classloader) {
        super(parent);
        this.classloader = classloader;
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(classloader);
    }

    public void addSCAPropertyElement(SpringSCAPropertyElement propertyElement) {
        propertyElements.add(propertyElement);
    }

    public void addSCAServiceElement(SpringSCAServiceElement serviceElement) {
        serviceElements.add(serviceElement);
    }

    public void addSCAReferenceElement(SpringSCAReferenceElement referenceElement) {
        referenceElements.add(referenceElement);
    }

    public synchronized List<SpringBeanElement> getBeanElements() {
        if (beanElements == null) {
            beanElements = new ArrayList<SpringBeanElement>();
            for (String name : getBeanDefinitionNames()) {
                BeanDefinition def = getBeanDefinition(name);
                SpringBeanElement beanElement = new SpringBeanElement(name, def.getBeanClassName());
                beanElements.add(beanElement);
                beanElement.setAbstractBean(def.isAbstract());
                beanElement.setFactoryBeanAttribute(def.getFactoryBeanName() != null);
                beanElement.setFactoryMethodAttribute(def.getFactoryMethodName() != null);
                beanElement.setParentAttribute(def.getParentName() != null);
                beanElement.setInnerBean(beanElement.getId() == null);

                ConstructorArgumentValues args = def.getConstructorArgumentValues();
                for (Map.Entry<Integer, ValueHolder> e: args.getIndexedArgumentValues().entrySet()) {
                    ValueHolder holder = e.getValue();
                    SpringConstructorArgElement arg = new SpringConstructorArgElement(holder.getType());
                    arg.setIndex(e.getKey());
                    beanElement.addCustructorArgs(arg);
                }

                MutablePropertyValues values = def.getPropertyValues();
                for (PropertyValue p : values.getPropertyValueList()) {
                    SpringPropertyElement propertyElement = new SpringPropertyElement(p.getName());
                    Object value = p.getValue();
                    configurePropertyElement(propertyElement, value);
                    beanElement.getProperties().add(propertyElement);
                }
            }
        }
        return beanElements;
    }

    public void configurePropertyElement(SpringPropertyElement propertyElement, Object value) {
        if (value instanceof BeanReference) {
            BeanReference beanRef = (BeanReference)value;
            propertyElement.addRef(beanRef.getBeanName());
        } else if (value instanceof Collection) {
            Collection collection = (Collection)value;
            for (Object item : collection) {
                configurePropertyElement(propertyElement, item);
            }
        } else if (value instanceof TypedStringValue) {
            TypedStringValue stringValue = (TypedStringValue)value;
            propertyElement.addValue(stringValue.getValue());
        } else {
            if (value != null) {
                propertyElement.addValue(value.toString());
            }
        }
    }

    public List<SpringSCAPropertyElement> getPropertyElements() {
        return propertyElements;
    }

    public List<SpringSCAServiceElement> getServiceElements() {
        return serviceElements;
    }

    public List<SpringSCAReferenceElement> getReferenceElements() {
        return referenceElements;
    }

}
