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

package org.apache.tuscany.interfacedef.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;

/**
 * The "Wrapper Style" WSDL operation is defined by The Java API for XML-Based
 * Web Services (JAX-WS) 2.0 specification, section 2.3.1.2 Wrapper Style. <p/>
 * A WSDL operation qualifies for wrapper style mapping only if the following
 * criteria are met:
 * <ul>
 * <li>(i) The operation�s input and output messages (if present) each contain
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
 */
public class WrapperInfo {
    private ElementInfo inputWrapperElement;

    private ElementInfo outputWrapperElement;

    private List<ElementInfo> inputChildElements;

    private List<ElementInfo> outputChildElements;

    private DataType<List<DataType>> unwrappedInputType;

    private DataType<XMLType> unwrappedOutputType;

    private String dataBinding;

    public WrapperInfo(String dataBinding,
                       ElementInfo inputWrapperElement,
                       ElementInfo outputWrapperElement,
                       List<ElementInfo> inputElements,
                       List<ElementInfo> outputElements) {
        super();
        this.dataBinding = dataBinding;
        this.inputWrapperElement = inputWrapperElement;
        this.outputWrapperElement = outputWrapperElement;
        this.inputChildElements = inputElements;
        this.outputChildElements = outputElements;
    }

    /**
     * @return the inputElements
     */
    public List<ElementInfo> getInputChildElements() {
        return inputChildElements;
    }

    /**
     * @return the inputWrapperElement
     */
    public ElementInfo getInputWrapperElement() {
        return inputWrapperElement;
    }

    /**
     * @return the outputElements
     */
    public List<ElementInfo> getOutputChildElements() {
        return outputChildElements;
    }

    /**
     * @return the outputWrapperElement
     */
    public ElementInfo getOutputWrapperElement() {
        return outputWrapperElement;
    }

    /**
     * @return the unwrappedInputType
     */
    public DataType<List<DataType>> getUnwrappedInputType() {
        if (unwrappedInputType == null) {
            List<DataType> childTypes = new ArrayList<DataType>();
            for (ElementInfo element : getInputChildElements()) {
                DataType<XMLType> type = new DataTypeImpl<XMLType>(dataBinding, Object.class, new XMLType(element));
                childTypes.add(type);
            }
            unwrappedInputType = new DataTypeImpl<List<DataType>>("idl:unwrapped.input", Object[].class,
                                                                           childTypes);
        }
        return unwrappedInputType;
    }

    /**
     * @return the unwrappedOutputType
     */
    public DataType getUnwrappedOutputType() {
        if (unwrappedOutputType == null) {
            List<ElementInfo> elements = getOutputChildElements();
            if (elements != null && elements.size() > 0) {
                if (elements.size() > 1) {
                    // We don't support output with multiple parts
                    throw new IllegalArgumentException("Multi-part output is not supported");
                }
                ElementInfo element = elements.get(0);

                unwrappedOutputType = new DataTypeImpl<XMLType>(dataBinding, Object.class, new XMLType(element));
            }
        }
        return unwrappedOutputType;
    }
}
