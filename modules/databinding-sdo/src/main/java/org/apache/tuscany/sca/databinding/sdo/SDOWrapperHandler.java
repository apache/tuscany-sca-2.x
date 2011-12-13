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

package org.apache.tuscany.sca.databinding.sdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

/**
 * SDO Wrapper Handler
 *
 * @version $Rev$ $Date$
 */
public class SDOWrapperHandler implements WrapperHandler<Object> {

    public Object create(Operation operation, boolean input) {
        WrapperInfo inputWrapperInfo = operation.getInputWrapper();
        WrapperInfo outputWrapperInfo = operation.getOutputWrapper();

        ElementInfo element = input ? inputWrapperInfo.getWrapperElement() : 
            outputWrapperInfo.getWrapperElement();
        
        HelperContext helperContext = SDOContextHelper.getHelperContext(operation);
        Type sdoType = getSDOType(helperContext, element);
        if (sdoType != null) {
            DataFactory dataFactory = helperContext.getDataFactory();
            return dataFactory.create(sdoType);
        }
        return null;
    }

    public void setChildren(Object wrapper, Object[] childObjects, Operation operation, boolean input) {
        WrapperInfo inputWrapperInfo = operation.getInputWrapper();
        WrapperInfo outputWrapperInfo = operation.getOutputWrapper();
        
        List<ElementInfo> childElements = input? inputWrapperInfo.getChildElements():
            outputWrapperInfo.getChildElements();

        for (int i = 0; i < childElements.size(); i++) {
            setChild(wrapper, i, childElements.get(i), childObjects[i]);
        }
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#setChild(java.lang.Object, int, ElementInfo,
     *      java.lang.Object)
     */
    public void setChild(Object wrapper, int i, ElementInfo childElement, Object value) {
        DataObject wrapperDO =
            (wrapper instanceof XMLDocument) ? ((XMLDocument)wrapper).getRootObject() : (DataObject)wrapper;
        String name = childElement.getQName().getLocalPart();
        if (childElement.isMany()) {
            // FIXME: If we look up by name, we need to make sure the WrapperInfo has the correct element names
            wrapperDO.getList(i).addAll((Collection)value);
        } else {
            wrapperDO.set(i, value);
        }
    }

    @SuppressWarnings("unchecked")
    public List getChildren(Object wrapper, Operation operation, boolean input) {
        DataObject wrapperDO =
            (wrapper instanceof XMLDocument) ? ((XMLDocument)wrapper).getRootObject() : (DataObject)wrapper;
        List<Property> properties = wrapperDO.getInstanceProperties();
        List<Object> elements = new ArrayList<Object>();
        Type type = wrapperDO.getType();
        if (type.isSequenced()) {
            // Add values in the sequence
            Sequence sequence = wrapperDO.getSequence();
            for (int i = 0; i < sequence.size(); i++) {
                // Skip mixed text
                if (sequence.getProperty(i) != null) {
                    elements.add(sequence.getValue(i));
                }
            }
        } else {
            for (Property p : properties) {
                Object child = wrapperDO.get(p);
                if (p.isMany()) {
                    for (Object c : (Collection<?>)child) {
                        elements.add(c);
                    }
                } else {
                    elements.add(child);
                }
            }
        }
        return elements;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(Operation, boolean)
     */
    public DataType getWrapperType(Operation operation, boolean input) {
        WrapperInfo inputWrapperInfo = operation.getInputWrapper();
        WrapperInfo outputWrapperInfo = operation.getOutputWrapper();

        ElementInfo element = input ? inputWrapperInfo.getWrapperElement() : 
            outputWrapperInfo.getWrapperElement();
        
        HelperContext helperContext = SDOContextHelper.getHelperContext(operation);
        Type sdoType = getSDOType(helperContext, element);
        if (sdoType != null) {
            // Check if child elements matches
            Class physical = sdoType.getInstanceClass();
            DataType<XMLType> wrapperType =
                new DataTypeImpl<XMLType>(SDODataBinding.NAME, physical, new XMLType(element));
            return wrapperType;
        } else {
            return null;
        }
    }

    /**
     * @param helperContext
     * @param element
     * @return
     */
    private Type getSDOType(HelperContext helperContext, ElementInfo element) {
        XSDHelper xsdHelper = helperContext.getXSDHelper();
        Type sdoType = null;
        Property prop =
            xsdHelper.getGlobalProperty(element.getQName().getNamespaceURI(), element.getQName().getLocalPart(), true);
        if (prop != null) {
            sdoType = prop.getType();
        } else {
            TypeInfo type = element.getType();
            QName typeName = type != null ? type.getQName() : null;
            if (typeName != null) {
                sdoType = helperContext.getTypeHelper().getType(typeName.getNamespaceURI(), typeName.getLocalPart());
            }
        }
        return sdoType;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#isInstance(java.lang.Object, Operation, boolean)
     */
    public boolean isInstance(Object wrapper, Operation operation, boolean input) {
        WrapperInfo inputWrapperInfo = operation.getInputWrapper();
        WrapperInfo outputWrapperInfo = operation.getOutputWrapper();

        ElementInfo element = input ? inputWrapperInfo.getWrapperElement() : 
            outputWrapperInfo.getWrapperElement();
        
        //        List<ElementInfo> childElements =
        //            input ? wrapperInfo.getInputChildElements() : wrapperInfo.getOutputChildElements();
        HelperContext helperContext = SDOContextHelper.getHelperContext(operation);
        Type sdoType = getSDOType(helperContext, element);
        if (sdoType != null) {
            return sdoType.isInstance(wrapper);
        }
        return false;
    }
}
