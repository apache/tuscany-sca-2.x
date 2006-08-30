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

package org.apache.tuscany.idl.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.DataType;

/**
 * Introspector for creating WSDLServiceContract definitions from WSDL PortTypes.
 */
public class InterfaceWSDLIntrospectorImpl implements InterfaceWSDLIntrospector {

    // FIXME: Do we want to deal with document-literal wrapped style based on the JAX-WS spec?
    protected Map<String, org.apache.tuscany.spi.model.Operation<QName>> introspectOperations(PortType portType)
        throws NotSupportedWSDLException {
        Map<String, org.apache.tuscany.spi.model.Operation<QName>> operations =
                new HashMap<String, org.apache.tuscany.spi.model.Operation<QName>>();
        for (Object op : portType.getOperations()) {
            Operation wsdlOp = (Operation) op;
            String name = wsdlOp.getName();

            Input input = wsdlOp.getInput();
            Message inputMsg = (input == null) ? null : input.getMessage();
            List<DataType<QName>> parameterTypes = introspectTypes(inputMsg);

            Message outputMsg = null;
            Output output = wsdlOp.getOutput();
            outputMsg = (output == null) ? null : output.getMessage();

            List outputParts = (outputMsg == null) ? null : outputMsg.getOrderedParts(null);
            DataType<QName> returnType = null;
            if (outputParts != null || outputParts.size() > 0) {
                if (outputParts.size() > 1) {
                    // We don't support output with multiple parts
                    throw new NotSupportedWSDLException("Multi-part output is not supported");
                }
                Part part = (Part) outputParts.get(0);
                returnType = introspectType(part);
            }

            Collection faults = wsdlOp.getFaults().values();
            List<DataType<QName>> faultTypes = new ArrayList<DataType<QName>>();
            for (Object f : faults) {
                Fault fault = (Fault) f;
                Message faultMsg = fault.getMessage();
                List faultParts = faultMsg.getOrderedParts(null);
                if (faultParts.size() != 1) {
                    throw new NotSupportedWSDLException("The fault message MUST have a single part");
                }
                Part part = (Part) faultParts.get(0);
                // A fault is typed by a message
                DataType<QName> dataType = introspectType(part);
                faultTypes.add(dataType);
            }

            // FIXME: [rfeng] How to figure the nonBlocking and dataBinding?
            org.apache.tuscany.spi.model.Operation<QName> operation =
                    new org.apache.tuscany.spi.model.Operation<QName>(name, returnType, parameterTypes, faultTypes,
                            true, null);
            operations.put(name, operation);
        }
        return operations;
    }

    protected List<DataType<QName>> introspectTypes(Message message) {
        List<DataType<QName>> dataTypes = new ArrayList<DataType<QName>>();
        if (message != null) {
            List parts = message.getOrderedParts(null);
            for (Object p : parts) {
                Part part = (Part) p;
                DataType<QName> dataType = introspectType(part);
                dataTypes.add(dataType);
            }
        }
        return dataTypes;
    }

    protected DataType<QName> introspectType(Part part) {
        QName partTypeName = part.getElementName();
        // FIXME: How can we get the corresponing type name for the element? We need the XSD model
        if (partTypeName == null) {
            partTypeName = part.getTypeName();
        }
        // FIXME: What java class is it? Should we try to see if there's a generated one?
        return new DataType<QName>(Object.class, partTypeName);
    }

    /**
     * @see org.apache.tuscany.idl.wsdl.InterfaceWSDLIntrospector#introspect(javax.wsdl.PortType)
     */
    public WSDLServiceContract introspect(PortType portType) throws InvalidServiceContractException {
        WSDLServiceContract contract = new WSDLServiceContract();
        contract.setPortType(portType);
        contract.setInterfaceName(portType.getQName().getLocalPart());
        contract.getOperations().putAll(introspectOperations(portType));
        return contract;
    }

    /**
     * @see org.apache.tuscany.idl.wsdl.InterfaceWSDLIntrospector#introspect(javax.wsdl.PortType, javax.wsdl.PortType)
     */
    public WSDLServiceContract introspect(PortType portType, PortType callbackPortType)
        throws InvalidServiceContractException {
        WSDLServiceContract contract = new WSDLServiceContract();
        contract.setPortType(portType);
        contract.setInterfaceName(portType.getQName().getLocalPart());
        contract.getOperations().putAll(introspectOperations(portType));
        contract.setCallbackPortType(callbackPortType);
        contract.setCallbackName(callbackPortType.getQName().getLocalPart());
        contract.getCallbacksOperations().putAll(introspectOperations(callbackPortType));
        return contract;
    }

}
