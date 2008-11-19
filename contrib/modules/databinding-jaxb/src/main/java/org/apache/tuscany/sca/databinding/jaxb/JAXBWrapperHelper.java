/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.databinding.jaxb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The JAXBWrapper tool is used to create a JAXB Object from a series of child objects (wrap) or get
 * the child objects from a JAXB Object (unwrap)
 */
public class JAXBWrapperHelper {

    /**
     * unwrap Returns the list of child objects of the jaxb object
     *
     * @param jaxbObject that represents the type
     * @param childNames list of xml child names as String
     * @param pdMap      PropertyDescriptor map for this jaxbObject
     * @return list of Objects in the same order as the element names.
     */
    public Object[] unwrap(Object jaxbObject, List<String> childNames, Map<String, JAXBPropertyDescriptor> pdMap)
        throws JAXBWrapperException {

        // Get the object that will have the property descriptors (i.e. the object representing the complexType)
        Object jaxbComplexTypeObj = jaxbObject;

        // Get the PropertyDescriptorPlus map.
        // The method makes sure that each child name has a matching jaxb property
        // checkPropertyDescriptorMap(jaxbComplexTypeObj.getClass(), childNames, pdMap);

        // Get the corresponsing objects from the jaxb bean
        ArrayList<Object> objList = new ArrayList<Object>();
        int index = 0;
        for (String childName : childNames) {
            JAXBPropertyDescriptor propInfo = getPropertyDescriptor(pdMap, childName, index);

            Object object = null;
            try {
                object = propInfo.get(jaxbComplexTypeObj);
            } catch (Throwable e) {
                throw new JAXBWrapperException(e);
            }

            objList.add(object);
            index++;
        }
        Object[] jaxbObjects = objList.toArray();
        objList = null;
        return jaxbObjects;

    }

    private JAXBPropertyDescriptor getPropertyDescriptor(Map<String, JAXBPropertyDescriptor> pdMap,
                                                         String childName,
                                                         int index) {
        JAXBPropertyDescriptor propInfo = pdMap.get(childName);
        if (propInfo == null) {
            // FIXME: [rfeng] Sometimes the child element names don't match. Get chilld by location?
            List<JAXBPropertyDescriptor> props = new ArrayList<JAXBPropertyDescriptor>(pdMap.values());
            // Sort the properties by index. We might need to take propOrder into consideration
            Collections.sort(props);
            propInfo = props.get(index);
        }
        return propInfo;
    }

    /**
     * wrap Creates a jaxb object that is initialized with the child objects.
     * <p/>
     * Note that the jaxbClass must be the class the represents the complexType. (It should never be
     * JAXBElement)
     *
     * @param jaxbClass
     * @param childNames    list of xml child names as String
     * @param childObjects, component type objects
     * @param pdMap         PropertyDescriptor map for this jaxbObject
     */
    public Object wrap(Class<?> jaxbClass,
                       List<String> childNames,
                       Map<String, Object> childObjects,
                       Map<String, JAXBPropertyDescriptor> pdMap) throws JAXBWrapperException {

        // Just like unWrap, get the property info map
        // checkPropertyDescriptorMap(jaxbClass, childNames, pdMap);

        // The jaxb object always has a default constructor.  Create the object
        Object jaxbObject = null;
        try {
            jaxbObject = jaxbClass.newInstance();
        } catch (Throwable t) {
            throw new JAXBWrapperException(t);
        }

        wrap(jaxbObject, childNames, childObjects, pdMap);

        // Return the jaxb object 
        return jaxbObject;
    }

    public void wrap(Object jaxbObject,
                     List<String> childNames,
                     Map<String, Object> childObjects,
                     Map<String, JAXBPropertyDescriptor> pdMap) {
        // Now set each object onto the jaxb object
        int index = 0;
        for (String childName : childNames) {
            JAXBPropertyDescriptor propInfo = getPropertyDescriptor(pdMap, childName, index);
            Object value = childObjects.get(childName);
            try {
                propInfo.set(jaxbObject, value);
            } catch (Throwable t) {
                throw new JAXBWrapperException(t);
            }
            index++;
        }
    }

    public Object[] unwrap(Object jaxbObject, List<String> childNames) throws JAXBWrapperException {
        // Get the property descriptor map for this JAXBClass
        Class<?> jaxbClass = jaxbObject.getClass();
        Map<String, JAXBPropertyDescriptor> pdMap = null;
        try {
            pdMap = XMLRootElementUtil.createPropertyDescriptorMap(jaxbClass);
        } catch (Throwable t) {
            throw new JAXBWrapperException(t);
        }

        // Delegate
        return unwrap(jaxbObject, childNames, pdMap);
    }

    public Object wrap(Class<?> jaxbClass, List<String> childNames, Map<String, Object> childObjects)
        throws JAXBWrapperException {
        // Get the property descriptor map
        Map<String, JAXBPropertyDescriptor> pdMap = null;
        try {
            pdMap = XMLRootElementUtil.createPropertyDescriptorMap(jaxbClass);
        } catch (Throwable t) {
            throw new JAXBWrapperException(t);
        }

        // Delegate
        return wrap(jaxbClass, childNames, childObjects, pdMap);
    }

}
