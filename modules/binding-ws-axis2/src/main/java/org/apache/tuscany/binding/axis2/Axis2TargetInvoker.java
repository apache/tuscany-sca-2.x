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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.implementation.java.invocation.TargetInvokerExtension;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.axis2.transport.http.HTTPConstants;

/**
 * Axis2TargetInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2TargetInvoker extends TargetInvokerExtension {

    private ServiceClient serviceClient;
    private QName wsdlOperationName;
    private Options options;
    private SOAPFactory soapFactory;

    public static final QName CONVERSATION_ID_REFPARM_QN = new QName(Constants.SCA10_NS,"conversationID");
    
    public Axis2TargetInvoker(ServiceClient serviceClient, QName wsdlOperationName, Options options, SOAPFactory soapFactory) {
        this.serviceClient = serviceClient;
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.soapFactory = soapFactory;
    }


    public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
        try {

            Object[] args = (Object[]) payload;
            OperationClient operationClient = createOperationClient(args, workContext);

            // ensure connections are tracked so that they can be closed by the reference binding
            MessageContext requestMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
            requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
            
            operationClient.execute(true);

            MessageContext responseMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            operationClient.complete(requestMC);

            return responseMC.getEnvelope().getBody().getFirstElement();

        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }
    }

    @SuppressWarnings("deprecation")
    protected OperationClient createOperationClient(Object[] args, WorkContext workContext) throws AxisFault {
        SOAPEnvelope env = soapFactory.getDefaultEnvelope();
        if (args != null && args.length > 0) {
            SOAPBody body = env.getBody();
            for (Object bc : args) {
                if (bc instanceof OMElement) {
                    body.addChild((OMElement) bc);
                } else {
                    throw new IllegalArgumentException(
                        "Can't handle mixed payloads betweem OMElements and other types.");
                }
            }
        }
        MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);

        // Axis2 operationClients can not be shared so create a new one for each request
        OperationClient operationClient = serviceClient.createClient(wsdlOperationName);

        if(workContext != null){
            String conversationId = (String) workContext.getIdentifier(Scope.CONVERSATION);
            if(conversationId != null && conversationId.length()!=0){
                EndpointReference fromEPR= new EndpointReference(AddressingConstants.Final.WSA_ANONYMOUS_URL);
                fromEPR.addReferenceParameter(CONVERSATION_ID_REFPARM_QN, conversationId);
                options.setFrom(fromEPR);
                requestMC.setFrom(fromEPR); //who knows why two ways ?
            
                //For now do this the brute force method. Need to figure out how to do axis addressing .. configure mar in flow.
                SOAPEnvelope sev = requestMC.getEnvelope();
                SOAPHeader sh = sev.getHeader();
                OMElement el= fromEPR.toOM(AddressingConstants.Final.WSA_NAMESPACE,AddressingConstants.WSA_FROM,AddressingConstants.WSA_DEFAULT_PREFIX);
                sh.addChild(el);
            }
        }

        operationClient.setOptions(options);
        operationClient.addMessageContext(requestMC);

        return operationClient;
    }

}
