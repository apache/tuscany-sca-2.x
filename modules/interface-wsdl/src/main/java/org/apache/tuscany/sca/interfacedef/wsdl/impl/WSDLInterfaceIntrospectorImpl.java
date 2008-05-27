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

package org.apache.tuscany.sca.interfacedef.wsdl.impl;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.PortType;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.xsd.XSDFactory;

/**
 * Introspector for creating WSDLInterface definitions from WSDL PortTypes.
 *
 * @version $Rev$ $Date$
 */
public class WSDLInterfaceIntrospectorImpl {
    
    private XSDFactory xsdFactory;
    
    public WSDLInterfaceIntrospectorImpl(XSDFactory xsdFactory) {
        this.xsdFactory = xsdFactory;
    }

    // FIXME: Do we want to deal with document-literal wrapped style based on the JAX-WS Specification?
    private List<Operation> introspectOperations(PortType portType, WSDLDefinition wsdlDefinition, ModelResolver resolver) throws InvalidWSDLException {
        List<Operation> operations = new ArrayList<Operation>();
        for (Object o : portType.getOperations()) {
            javax.wsdl.Operation wsdlOp = (javax.wsdl.Operation)o;
            operations.add(getOperation(wsdlOp, wsdlDefinition, resolver, xsdFactory));
        }
        return operations;
    }

    public void introspectPortType(WSDLInterface wsdlInterface, PortType portType, WSDLDefinition wsdlDefinition, ModelResolver resolver) throws InvalidWSDLException {
        wsdlInterface.setPortType(portType);
        wsdlInterface.getOperations().addAll(introspectOperations(portType, wsdlDefinition, resolver));
        // FIXME: set to Non-conversational for now
        wsdlInterface.setConversational(false);
    }

    public static Operation getOperation(javax.wsdl.Operation wsdlOp,
                                         WSDLDefinition wsdlDefinition,
                                         ModelResolver resolver,
                                         XSDFactory xsdFactory) throws InvalidWSDLException {
        WSDLOperationIntrospectorImpl op = new WSDLOperationIntrospectorImpl(xsdFactory, wsdlOp, wsdlDefinition, null, resolver);
        return op.getOperation();
    }
    
}
