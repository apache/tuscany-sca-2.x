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
package org.apache.tuscany.standalone.server.management.jmx.instrument.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * Uses JMX dynamic MBean to expose management information of a delegate
 * instance using reflection. Currently constructor and notification 
 * metadata are not supported. Any attribute or operation that needs to 
 * be excluded from the management information can be specified optionally 
 * in the factory method. 
 * 
 * All the methods and properties on <code>java.lang.Object</code> are 
 * excluded by default. Also only public and non-static members are made 
 * available for management.
 * 
 * TODO Find a homw other than server.start for this class.
 * TODO Tidy up, unit tests, exception handling
 * 
 * @version $Revsion$ $Date$
 *
 */
public class ReflectedDynamicMBean implements DynamicMBean {

    /** Excluded methods. */
    private static final List<String> EXCLUDED_METHODS =
        Arrays.asList(new String[] {"wait", "toString", "hashCode", "notify", "equals", "notifyAll", "getClass"});

    /** Excluded properties. */
    private static final List<String> EXCLUDED_PROPERTIES = Arrays.asList(new String[] {"class"});

    /** Proxied object that is managed. */
    private Object delegate;

    /** Runtime type of the managed object. */
    private Class delegateClass;

    /** Cache of property write methods. */
    private Map<String, Method> propertyWriteMethods = new HashMap<String, Method>();

    /** Cache of property read methods. */
    private Map<String, Method> propertyReadMethods = new HashMap<String, Method>();

    /** Managed operation cache. */
    private Map<String, Method> methods = new HashMap<String, Method>();

    /** Property descriptor cache. */
    private Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();

    public ReflectedDynamicMBean(Object delegate) {

        this.delegate = delegate;
        this.delegateClass = delegate.getClass();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(delegateClass);
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        cacheProperties(beanInfo);

        cacheMethods(beanInfo);
    }

    private void cacheMethods(BeanInfo beanInfo) {
        
        for (MethodDescriptor methodDescriptor : beanInfo.getMethodDescriptors()) {

            Method method = methodDescriptor.getMethod();

            if (EXCLUDED_METHODS.contains(method.getName())) {
                continue;
            }
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }
            if (propertyReadMethods.values().contains(method) || propertyWriteMethods.values().contains(method)) {
                continue;
            }

            StringBuilder signature = new StringBuilder(method.getName());
            signature.append('(');
            for (Class parameterType : method.getParameterTypes()) {
                signature.append(parameterType.getName());
                signature.append(',');
            }
            int length = signature.length();
            if (signature.charAt(signature.length() - 1) == ',') {
                signature.replace(length - 1, length, ")");
            } else {
                signature.append(')');
            }

            methods.put(signature.toString(), method);
            
        }
        
    }

    private void cacheProperties(BeanInfo beanInfo) {
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {

            String name = propertyDescriptor.getName();

            if (EXCLUDED_PROPERTIES.contains(name)) {
                continue;
            }
            properties.put(name, propertyDescriptor);

            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && Modifier.isPublic(readMethod.getModifiers())) {
                propertyReadMethods.put(name, readMethod);
            }

            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null && Modifier.isPublic(writeMethod.getModifiers())) {
                propertyWriteMethods.put(name, writeMethod);
            }

        }
    }

    /**
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {

        Method readMethod = propertyReadMethods.get(attribute);
        if (readMethod == null) {
            throw new AttributeNotFoundException(attribute + " not found");
        }
        try {
            return readMethod.invoke(delegate);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(ex);
        }

    }

    /**
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
     */
    public AttributeList getAttributes(String[] attributes) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    public MBeanInfo getMBeanInfo() {

        try {

            MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[properties.keySet().size()];
            int count = 0;
            for (String property : properties.keySet()) {
                attributes[count++] =
                    new MBeanAttributeInfo(property, "", propertyReadMethods.get(property), propertyWriteMethods
                        .get(property));
            }

            MBeanOperationInfo[] operations = new MBeanOperationInfo[methods.keySet().size()];
            count = 0;
            for (Method method : methods.values()) {
                operations[count++] = new MBeanOperationInfo("", method);
            }

            MBeanInfo mBeanInfo =
                new MBeanInfo(delegateClass.getName(), "", attributes, null, operations, null);
            return mBeanInfo;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
    }

    /**
     * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
     */
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
        ReflectionException {
        return null;
    }

    /**
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
        MBeanException, ReflectionException {

        Method writeMethod = propertyWriteMethods.get(attribute.getName());
        if (writeMethod == null) {
            throw new AttributeNotFoundException(attribute + " not found");
        }
        try {
            writeMethod.invoke(delegate, attribute.getValue());
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(ex);
        }

    }

    /**
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
     */
    public AttributeList setAttributes(AttributeList attributes) {
        throw new UnsupportedOperationException();
    }

}
