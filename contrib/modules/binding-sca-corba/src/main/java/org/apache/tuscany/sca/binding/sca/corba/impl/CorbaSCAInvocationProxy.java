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

package org.apache.tuscany.sca.binding.sca.corba.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.service.InvocationException;
import org.apache.tuscany.sca.binding.corba.impl.service.InvocationProxy;
import org.apache.tuscany.sca.binding.corba.impl.service.OperationTypes;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Invocation proxy for SCA default binding over CORBA binding
 */
public class CorbaSCAInvocationProxy implements InvocationProxy {

    private static final Logger logger = Logger.getLogger(CorbaSCAInvocationProxy.class.getName());
    private RuntimeWire wire;
    private Interface componentInterface;
    private OperationTypes types = new OperationTypes();
    private MessageFactory messageFactory;

    public CorbaSCAInvocationProxy(RuntimeWire wire, Interface componentInterface, MessageFactory messageFactory) {
        this.wire = wire;
        this.componentInterface = componentInterface;
        this.messageFactory = messageFactory;
        try {
            List<TypeTree> inputType = new ArrayList<TypeTree>();
            inputType.add(TypeTreeCreator.createTypeTree(String.class, null));
            types.setInputType(inputType);
            types.setOutputType(TypeTreeCreator.createTypeTree(String.class, null));
        } catch (RequestConfigurationException e) {
            // ignore - string type should not cause this exception
        }
    }

    public Object invoke(String operationName, List<Object> arguments) throws InvocationException {
        try {
            OMElement omContent = CorbaSCAInvoker.stringToOM((String)arguments.get(0));
            String componentOperationName = omContent.getQName().getLocalPart();
            Operation componentOperation = null;
            for (Operation operation : componentInterface.getOperations()) {
                if (operation.getName().equals(componentOperationName)) {
                    componentOperation = operation;
                    break;
                }
            }
            if (componentOperation != null) {
                Message msg = messageFactory.createMessage();
                msg.setOperation(componentOperation);
                Object[] args = new Object[1];
                args[0] = omContent;
                msg.setBody(args);
                OMElement omResult = (OMElement)wire.invoke(componentOperation, msg);
                return omResult.toStringWithConsume();
            }
        } catch (XMLStreamException e) {
            logger.log(Level.WARNING, "XMLStreamException during handling invocation target exception", e);
        } catch (InvocationTargetException e) {
            OMElement omException = (OMElement)((FaultException)e.getCause()).getFaultInfo();
            try {
                WrappedSCAException wrappedException = new WrappedSCAException(omException.toStringWithConsume());
                InvocationException exception = new InvocationException(wrappedException);
                throw exception;
            } catch (XMLStreamException xmle) {
                logger.log(Level.WARNING, "XMLStreamException during handling invocation target exception", xmle);
            }
            
        }

        return null;
    }

    public OperationTypes getOperationTypes(String operationName) {
        // ignoring operationName - only one operation for this proxy is allowed
        return types;
    }

}
