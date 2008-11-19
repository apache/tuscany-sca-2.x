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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;

/**
 * JAXB WrapperHandler implementation
 *
 * @version $Rev$ $Date$
 */
public class JAXBWrapperHandler implements WrapperHandler<Object> {
    private JAXBWrapperHelper helper = new JAXBWrapperHelper();

    public Object create(Operation operation, boolean input) {
        WrapperInfo wrapperInfo = operation.getWrapper();
        ElementInfo element = input ? wrapperInfo.getInputWrapperElement() : wrapperInfo.getOutputWrapperElement();
        final Class<?> wrapperClass = input ? wrapperInfo.getInputWrapperClass() : wrapperInfo.getOutputWrapperClass();
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

    public void setChildren(Object wrapper, Object[] childObjects, Operation operation, boolean input) {
        List<ElementInfo> childElements =
            input ? operation.getWrapper().getInputChildElements() : operation.getWrapper().getOutputChildElements();
        List<String> childNames = new ArrayList<String>();
        Map<String, Object> values = new HashMap<String, Object>();
        for (int i = 0; i < childElements.size(); i++) {
            ElementInfo e = childElements.get(i);
            String name = e.getQName().getLocalPart();
            childNames.add(name);
            values.put(name, childObjects[i]);
        }
        // Get the property descriptor map
        Map<String, JAXBPropertyDescriptor> pdMap = null;
        try {
            pdMap = XMLRootElementUtil.createPropertyDescriptorMap(wrapper.getClass());
        } catch (Throwable t) {
            throw new JAXBWrapperException(t);
        }
        helper.wrap(wrapper, childNames, values, pdMap);
    }

    public void setChild(Object wrapper, int i, ElementInfo childElement, Object value) {
        Object wrapperValue = wrapper;
        Class<?> wrapperClass = wrapperValue.getClass();

        // FIXME: We probably should use the jaxb-reflection to handle the properties
        try {
            String prop = childElement.getQName().getLocalPart();
            boolean collection = (value instanceof Collection);
            Method getter = null;
            for (Method m : wrapperClass.getMethods()) {
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length == 1 && m.getName().equals("set" + capitalize(prop))) {
                    m.invoke(wrapperValue, new Object[] {value});
                    return;
                }
                if (collection && paramTypes.length == 0 && m.getName().equals("get" + capitalize(prop))) {
                    getter = m;
                }
            }
            if (getter != null && Collection.class.isAssignableFrom(getter.getReturnType())) {
                ((Collection)getter.invoke(wrapperValue)).addAll((Collection)value);
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
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getChildren(java.lang.Object, Operation, boolean)
     */
    public List getChildren(Object wrapper, Operation operation, boolean input) {
        List<ElementInfo> childElements = input? operation.getWrapper().getInputChildElements():
            operation.getWrapper().getOutputChildElements();

        List<String> childNames = new ArrayList<String>();
        for (ElementInfo e : childElements) {
            childNames.add(e.getQName().getLocalPart());
        }
        return Arrays.asList(helper.unwrap(wrapper, childNames));
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(Operation, boolean)
     */
    public DataType getWrapperType(Operation operation, boolean input) {
        WrapperInfo wrapper = operation.getWrapper();
        DataType dt = input ? wrapper.getInputWrapperType() : wrapper.getOutputWrapperType();
        return dt;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#isInstance(java.lang.Object, Operation, boolean)
     */
    public boolean isInstance(Object wrapper, Operation operation, boolean input) {
        Class<?> wrapperClass =
            input ? operation.getWrapper().getInputWrapperClass() : operation.getWrapper().getOutputWrapperClass();
        return wrapperClass == null ? false : wrapperClass.isInstance(wrapper);
    }
}
