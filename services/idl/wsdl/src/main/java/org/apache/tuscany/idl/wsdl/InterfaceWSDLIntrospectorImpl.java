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

import java.util.HashMap;
import java.util.Map;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;

/**
 * Introspector for creating WSDLServiceContract definitions from WSDL PortTypes.
 */
public class InterfaceWSDLIntrospectorImpl implements InterfaceWSDLIntrospector {
    public static final String INPUT_PARTS = "idl:input";

    private WSDLDefinitionRegistry wsdlDefinitionRegistry;

    /**
     * @param wsdlDefinitionRegistry
     */
    @Constructor
    public InterfaceWSDLIntrospectorImpl(@Reference WSDLDefinitionRegistry wsdlDefinitionRegistry) {
        super();
        this.wsdlDefinitionRegistry = wsdlDefinitionRegistry;
    }

    // FIXME: Do we want to deal with document-literal wrapped style based on the JAX-WS spec?
    protected Map<String, org.apache.tuscany.spi.model.Operation<QName>> introspectOperations(PortType portType)
        throws InvalidServiceContractException {
        Map<String, org.apache.tuscany.spi.model.Operation<QName>> operations =
            new HashMap<String, org.apache.tuscany.spi.model.Operation<QName>>();
        for (Object op : portType.getOperations()) {
            Operation wsdlOp = (Operation) op;
            operations.put(wsdlOp.getName(), introspectOperation(wsdlOp));
        }
        return operations;
    }

    protected org.apache.tuscany.spi.model.Operation<QName> introspectOperation(Operation wsdlOp)
        throws InvalidServiceContractException {

        WSDLOperation op = new WSDLOperation(wsdlOp, null, wsdlDefinitionRegistry.getSchemaRegistry());
        return op.getOperation();
    }

    /**
     * @see org.apache.tuscany.idl.wsdl.InterfaceWSDLIntrospector#introspect(javax.wsdl.PortType)
     */
    public WSDLServiceContract introspect(PortType portType) throws InvalidServiceContractException {
        WSDLServiceContract contract = new WSDLServiceContract();
        contract.setPortType(portType);
        contract.setInterfaceName(portType.getQName().getLocalPart());
        contract.setOperations(introspectOperations(portType));
        // FIXME: set to Non-conversational for now
        contract.setConversational(false);
        return contract;
    }

    /**
     * @see org.apache.tuscany.idl.wsdl.InterfaceWSDLIntrospector#introspect(javax.wsdl.PortType,javax.wsdl.PortType)
     */
    public WSDLServiceContract introspect(PortType portType, PortType callbackPortType)
        throws InvalidServiceContractException {
        assert portType != null : "PortType cannot be null";
        WSDLServiceContract contract = new WSDLServiceContract();
        // FIXME: set to Non-conversational for now
        contract.setConversational(false);
        contract.setPortType(portType);
        contract.setInterfaceName(portType.getQName().getLocalPart());
        contract.setOperations(introspectOperations(portType));
        if (callbackPortType != null) {
            contract.setCallbackPortType(callbackPortType);
            contract.setCallbackName(callbackPortType.getQName().getLocalPart());
            contract.setCallbackOperations(introspectOperations(callbackPortType));
        }
        return contract;
    }

}
