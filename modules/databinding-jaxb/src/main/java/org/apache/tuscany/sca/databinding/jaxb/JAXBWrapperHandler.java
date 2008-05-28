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

package org.apache.tuscany.sca.databinding.jaxb;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * JAXB WrapperHandler implementation
 *
 * @version $Rev$ $Date$
 */
public class JAXBWrapperHandler implements WrapperHandler<Object> {

    public Object create(ElementInfo element, final Class<? extends Object> wrapperClass, TransformationContext context) {
        try {
            if (wrapperClass == null) {
                return null;
            }
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    return wrapperClass.newInstance();
                }
            });
        } catch (PrivilegedActionException e) {
            throw new TransformationException(e);
        }
    }

    public void setChild(Object wrapper, int i, ElementInfo childElement, Object value) {
        Object wrapperValue = wrapper;
        Class<?> wrapperClass = wrapperValue.getClass();

        XmlType xmlType = wrapperClass.getAnnotation(XmlType.class);
        String[] properties = xmlType.propOrder();
        String property = properties[i];

        try {
            for (Method m : wrapperClass.getMethods()) {
                if (m.getName().equals("set" + capitalize(property))) {
                    m.invoke(wrapperValue, new Object[] {value});
                    return;
                }
            }
        } catch (Throwable e) {
            throw new TransformationException(e);
        }
    }

    private static String capitalize(String name) {
        char first = Character.toUpperCase(name.charAt(0));
        return first + name.substring(1);
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getChildren(java.lang.Object, List, TransformationContext)
     */
    public List getChildren(Object wrapper, List<ElementInfo> childElements, TransformationContext context) {
        Object wrapperValue = wrapper;
        Class<?> wrapperClass = wrapperValue.getClass();

        XmlType xmlType = wrapperClass.getAnnotation(XmlType.class);
        String[] properties = xmlType.propOrder();
        List<Object> elements = new ArrayList<Object>();
        for (String p : properties) {
            try {
                Method method = wrapperClass.getMethod("get" + capitalize(p), (Class[])null);
                Object value = method.invoke(wrapperValue, (Object[])null);
                elements.add(value);
            } catch (Throwable e) {
                throw new TransformationException(e);
            }
        }
        return elements;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(org.apache.tuscany.sca.interfacedef.util.ElementInfo, Class, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public DataType getWrapperType(ElementInfo element,
                                   Class<? extends Object> wrapperClass,
                                   TransformationContext context) {
        if (wrapperClass == null) {
            return null;
        } else {
            return new DataTypeImpl<XMLType>(JAXBDataBinding.NAME, wrapperClass, new XMLType(element));
        }
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#isInstance(java.lang.Object, org.apache.tuscany.sca.interfacedef.util.ElementInfo, java.util.List, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public boolean isInstance(Object wrapper,
                              ElementInfo element,
                              List<ElementInfo> childElements,
                              TransformationContext context) {
        // TODO: Implement the logic
        return true;
    }
}
