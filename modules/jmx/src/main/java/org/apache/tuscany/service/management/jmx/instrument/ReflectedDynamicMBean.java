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
package org.apache.tuscany.service.management.jmx.instrument;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
 * Uses JMX dynamic MBean to expose management information of a delegate instance using reflection. Currently
 * constructor and notification metadata are not supported. Any attribute or operation that needs to be excluded from
 * the management information can be specified optionally in the factory method.
 * <p/>
 * All the methods and properties on <code>java.lang.Object</code> are excluded by default. Also only public and
 * non-static members are made available for management.
 * <p/>
 * TODO Find a homw other than server.start for this class. TODO Tidy up, unit tests
 *
 * @version $Revsion$ $Date$
 */
public class ReflectedDynamicMBean implements DynamicMBean {

    /**
     * Excluded methods.
     */
    private static final List<String> DEFAULT_EXCLUDED_METHODS =
        Arrays.asList(new String[]{"wait", "toString", "hashCode", "notify", "equals", "notifyAll", "getClass"});

    /**
     * Excluded properties.
     */
    private static final List<String> DEFAULT_EXCLUDED_PROPERTIES = Arrays.asList(new String[]{"class"});

    /**
     * Proxied object that is managed.
     */
    private Object delegate;

    /**
     * Runtime type of the managed object.
     */
    private Class delegateClass;

    /**
     * Delegate class name.
     */
    private String delegateClassName;

    /**
     * Cache of property write methods.
     */
    private Map<String, Method> propertyWriteMethods = new HashMap<String, Method>();

    /**
     * Cache of property read methods.
     */
    private Map<String, Method> propertyReadMethods = new HashMap<String, Method>();

    /**
     * Managed operation cache.
     */
    private Map<String, Method> methods = new HashMap<String, Method>();

    /**
     * Property descriptor cache.
     */
    private Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();

    /**
     * Excluded methods.
     */
    private final List<String> excludedMethods = new ArrayList<String>();

    /**
     * Excluded properties.
     */
    private final List<String> excludedProperties = new ArrayList<String>();

    /**
     * Introspects the bean and populate meta information.
     *
     * @param delegate Proxied managed instance.
     */
    private ReflectedDynamicMBean(Object delegate) {
        this(delegate, new ArrayList<String>(), new ArrayList<String>());
    }

    /**
     * Introspects the bean and populate meta information.
     *
     * @param delegate           Proxied managed instance.
     * @param excludedMethods    Operations excluded from managed view.
     * @param excludedProperties Properties excluded from managed view.
     */
    private ReflectedDynamicMBean(Object delegate, List<String> excludedMethods, List<String> excludedProperties) {

        this.delegate = delegate;
        this.delegateClass = delegate.getClass();
        this.delegateClassName = delegateClass.getName();

        this.excludedMethods.addAll(excludedMethods);
        this.excludedMethods.addAll(DEFAULT_EXCLUDED_METHODS);
        this.excludedProperties.addAll(excludedProperties);
        this.excludedProperties.addAll(DEFAULT_EXCLUDED_PROPERTIES);

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(delegateClass);
        } catch (IntrospectionException ex) {
            throw new InstrumentationException(ex);
        }

        cacheProperties(beanInfo);

        cacheMethods(beanInfo);
    }

    /**
     * Factory method for creating the management view.
     *
     * @param delegate           Proxied managed instance.
     * @param excludedMethods    Operations excluded from managed view.
     * @param excludedProperties Properties excluded from managed view.
     * @return Proxy for the managed instance.
     */
    public static ReflectedDynamicMBean newInstance(Object delegate, List<String> excludedMethods,
                                                    List<String> excludedProperties) {
        return new ReflectedDynamicMBean(delegate, excludedMethods, excludedProperties);
    }

    /**
     * Factory method for creating the management view.
     *
     * @param delegate Proxied managed instance.
     * @return Proxy for the managed instance.
     */
    public static ReflectedDynamicMBean newInstance(Object delegate) {
        return new ReflectedDynamicMBean(delegate);
    }

    /**
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute)
        throws AttributeNotFoundException, MBeanException, ReflectionException {

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

        AttributeList list = new AttributeList();
        for (String attribute : attributes) {
            try {
                list.add(new Attribute(attribute, getAttribute(attribute)));
            } catch (AttributeNotFoundException ex) {
                throw new InstrumentationException(ex);
            } catch (MBeanException ex) {
                throw new InstrumentationException(ex);
            } catch (ReflectionException ex) {
                throw new InstrumentationException(ex);
            }
        }
        return list;

    }

    /**
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    public MBeanInfo getMBeanInfo() {

        try {

            MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[properties.keySet().size()];
            int count = 0;
            for (String property : properties.keySet()) {
                Method readMethod = propertyReadMethods.get(property);
                Method writeMethod = propertyWriteMethods.get(property);
                attrs[count++] = new MBeanAttributeInfo(property, "", readMethod, writeMethod);
            }

            MBeanOperationInfo[] ops = new MBeanOperationInfo[methods.keySet().size()];
            count = 0;
            for (Method method : methods.values()) {
                ops[count++] = new MBeanOperationInfo("", method);
            }

            MBeanInfo mBeanInfo = new MBeanInfo(delegateClassName, "", attrs, null, ops, null);
            return mBeanInfo;

        } catch (javax.management.IntrospectionException ex) {
            throw new InstrumentationException(ex);
        }

    }

    /**
     * @see javax.management.DynamicMBean#invoke(java.lang.String,java.lang.Object[],java.lang.String[])
     */
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
                                                                                        ReflectionException {

        Method method = methods.get(actionName);
        if (method == null) {
            throw new InstrumentationException("Operation not found: " + actionName);
        }
        try {
            return method.invoke(delegate, params);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(ex);
        }

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

    /**
     * Caches managed operations.
     *
     * @param beanInfo Bean info for the managed instance.
     */
    private void cacheMethods(BeanInfo beanInfo) {

        for (MethodDescriptor methodDescriptor : beanInfo.getMethodDescriptors()) {

            Method method = methodDescriptor.getMethod();
            String name = method.getName();

            if (excludedMethods.contains(name)) {
                continue;
            }
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }
            if (propertyReadMethods.values().contains(method) || propertyWriteMethods.values().contains(method)) {
                continue;
            }

            // TODO Add support for overloaded methods
            methods.put(name, method);

        }

    }

    /**
     * Caches managed properties.
     *
     * @param beanInfo Bean info for the managed instance.
     */
    private void cacheProperties(BeanInfo beanInfo) {
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {

            String name = propertyDescriptor.getName();

            if (excludedProperties.contains(name)) {
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

}
