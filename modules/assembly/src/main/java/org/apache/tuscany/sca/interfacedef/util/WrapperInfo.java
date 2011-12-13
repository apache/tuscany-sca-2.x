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

package org.apache.tuscany.sca.interfacedef.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

/**
 * The "Wrapper Style" WSDL operation is defined by The Java API for XML-Based
 * Web Services (JAX-WS) 2.0 specification, section 2.3.1.2 Wrapper Style. <p/>
 * A WSDL operation qualifies for wrapper style mapping only if the following
 * criteria are met:
 * <ul>
 * <li>(i) The operations input and output messages (if present) each contain
 * only a single part
 * <li>(ii) The input message part refers to a global element declaration whose
 * localname is equal to the operation name
 * <li>(iii) The output message part refers to a global element declaration
 * <li>(iv) The elements referred to by the input and output message parts
 * (henceforth referred to as wrapper elements) are both complex types defined
 * using the xsd:sequence compositor
 * <li>(v) The wrapper elements only contain child elements, they must not
 * contain other structures such as wildcards (element or attribute),
 * xsd:choice, substitution groups (element references are not permitted) or
 * attributes; furthermore, they must not be nillable.
 * </ul>
 * 
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public class WrapperInfo implements Cloneable {
    
    // The databinding for the wrapper
    private String dataBinding;
    
    // The XML element representation of the wrapper
    private ElementInfo wrapperElement;
    
    // The XML child elements of the wrapper
    private List<ElementInfo> childElements;
    
    // The data type for the wrapper bean
    private DataType<XMLType> wrapperType;
    
    // The data types of the unwrapped child elements 
    private DataType<List<DataType>> unwrappedType;

    public WrapperInfo(String dataBinding,
                       ElementInfo wrapperElement,
                       List<ElementInfo> childElements) {
        super();
        this.dataBinding = dataBinding;
        this.wrapperElement = wrapperElement;
        this.childElements = childElements;
    }

    /**
     * Get the list of XML child elements that this 
     * wrapper wraps
     * 
     * @return the childElements
     */
    public List<ElementInfo> getChildElements() {
        return childElements;
    }

    /**
     * Get the XML element that represents this wrapper 
     * 
     * @return the wrapperElement
     */
    public ElementInfo getWrapperElement() {
        return wrapperElement;
    }
    
    /**
     * Get the databinding that this wrapper will
     * be subject to
     * 
     * @return dataBinding
     */
    public String getDataBinding() {
        return dataBinding;
    }

    /**
     * Set the databinding that this wrapper will
     * be subject to
     * 
     * @param dataBinding
     */
    public void setDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }    
   
    /**
     * Get the Tuscany data type for the wrapper
     * 
     * @return Tuscany data type for the wrapper
     */
    public DataType<XMLType> getWrapperType() {
        return wrapperType;
    }

    /**
     * Set the Tuscany data type for the wrapper
     * 
     * @param wrapperType Tuscany data type for the wrapper
     */
    public void setWrapperType(DataType<XMLType> wrapperType) {
        this.wrapperType = wrapperType;
    }   
    
    /**
     * Return the Java class for the wrapper
     * 
     * @return Java class for the wrapper
     */
    public Class<?> getWrapperClass() {
        return wrapperType == null ? null : wrapperType.getPhysical();
    }    

    @Override
    public Object clone() throws CloneNotSupportedException {
        WrapperInfo copy = (WrapperInfo) super.clone();
        if (wrapperType != null) {
            copy.wrapperType = (DataType<XMLType>)wrapperType.clone();
        }
        return copy;

    }
 
    /**
     * Creates and caches the data types for the child elements 
     * 
     * @return The list of child element data types
     */
    public DataType<List<DataType>> getUnwrappedType() {
        if (unwrappedType == null) {
            List<DataType> childTypes = new ArrayList<DataType>();
            for (ElementInfo element : getChildElements()) {
                DataType type = getDataType(element);
                childTypes.add(type);
            }
            unwrappedType = new DataTypeImpl<List<DataType>>("idl:unwrapped", Object[].class, childTypes);
        }
        return unwrappedType;
    }  

    private DataType getDataType(ElementInfo element) {
        DataType type = null;
        if (element.isMany()) {
            DataType logical = new DataTypeImpl<XMLType>(dataBinding, Object.class, new XMLType(element));
            type = new DataTypeImpl<DataType>("java:array", Object[].class, logical);
        } else {
            type = new DataTypeImpl<XMLType>(dataBinding, Object.class, new XMLType(element));
        }
        return type;
    }

}
