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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaRequest;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaResponse;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.omg.CORBA.Object;
import org.osoa.sca.ServiceRuntimeException;

public class CorbaSCAInvoker implements Invoker {

    private Object remoteObject;
    private Class<?> referenceClass;

    public CorbaSCAInvoker(Object remoteObject,
                           Class<?> referenceClass,
                           Map<Method, String> operationsMap,
                           boolean scaBindingRules) {
        this.remoteObject = remoteObject;
        this.referenceClass = referenceClass;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {
        try { 
            DynaCorbaRequest request = new DynaCorbaRequest(remoteObject, "scaService");
            request.setReferenceClass(referenceClass);
            request.setOutputType(String.class);
            request.addExceptionType(WrappedSCAException.class);
            java.lang.Object[] args = msg.getBody();
            OMElement omElement = (OMElement)args[0];
            String arg = omElement.toStringWithConsume();
            request.addArgument(arg);
            DynaCorbaResponse response = request.invoke();
            OMElement responseOM = stringToOM((String)response.getContent());
            msg.setBody(responseOM);
        } catch (WrappedSCAException e) {
            try {
                OMElement exceptionOM = stringToOM(e.getFault());
                // AxisFault axisFault = new AxisFault("");
                // axisFault.setDetail(exceptionOM);
                FaultException f = new FaultException(e.getMessage(), exceptionOM, e);
                f.setFaultName(exceptionOM.getQName());
                msg.setFaultBody(f);
            } catch (XMLStreamException e1) {
            }
        } catch (RequestConfigurationException e) {
            throw new ServiceRuntimeException(e);
        } catch (Exception e) {
            msg.setFaultBody(e);
        }
        return msg;
    }
    
    /**
     * This will help you to create an OMElement from an xml fragment which you have as a string.
     *
     * @param xmlFragment - the well-formed xml fragment
     * @return The OMElement created out of the string xml fragment.
     * @throws XMLStreamException
     */
    static OMElement stringToOM(String xmlFragment) throws XMLStreamException {
        if (xmlFragment != null) {
            return new StAXOMBuilder(new ByteArrayInputStream(xmlFragment.getBytes())).getDocumentElement();
        }
        return null;
    }
}
