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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class Axis2ServiceProvider  implements Provider<OMElement> {
    private static final Logger logger = Logger.getLogger(Axis2ServiceProvider.class.getName());
    
    public static final QName QNAME_WSA_ADDRESS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_ADDRESS);
    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_REFERENCE_PARAMETERS =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.EPR_REFERENCE_PARAMETERS);
    
    
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    private MessageFactory messageFactory;
    private FactoryExtensionPoint modelFactories;
    private RuntimeAssemblyFactory assemblyFactory;
    
    @Resource
    WebServiceContext wsContext;
    
    public Axis2ServiceProvider(RuntimeEndpoint endpoint,
                                WebServiceBinding wsBinding,
                                ExtensionPointRegistry extensionPoints) {
        this.endpoint = endpoint;
        this.wsBinding = wsBinding;
        this.modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.messageFactory = modelFactories.getFactory(MessageFactory.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);        
    }
    
    public OMElement invoke(OMElement arg0) {
        return null;
    }
    

    public Object invokeTarget(Operation op, Object[] args, MessageContext inMC) throws InvocationTargetException {
        String callbackAddress = null;
        String callbackID = null;

        // create a message object and set the args as its body
        Message msg = messageFactory.createMessage();
        msg.setBody(args);
        msg.setOperation(op);
        msg.setBindingContext(inMC);

        //FIXME: can we use the Axis2 addressing support for this?
        SOAPHeader header = inMC.getEnvelope().getHeader();
        if (header != null) {
            OMElement from = header.getFirstChildWithName(QNAME_WSA_FROM);
            if (from != null) {
                OMElement callbackAddrElement = from.getFirstChildWithName(QNAME_WSA_ADDRESS);
                if (callbackAddrElement != null) {
                    if (endpoint.getService().getInterfaceContract().getCallbackInterface() != null) {
                        callbackAddress = callbackAddrElement.getText();
                    }
                }
            }

            // get policy specified headers
/*            
            for (Axis2HeaderPolicy policy : axis2HeaderPolicies) {
                //Axis2BindingHeaderConfigurator.getHeader(inMC, msg, policy.getHeaderName());
            }
*/            
/* 
            if (axis2TokenAuthenticationPolicy != null) {
                Axis2SOAPHeaderString tokenHeader = new Axis2SOAPHeaderString();
               
                Axis2BindingHeaderConfigurator.getHeader(inMC,
                                                         msg,
                                                         axis2TokenAuthenticationPolicy.getTokenName(),
                                                         tokenHeader);                                           
            }
*/            
        }

        // Create a from EPR to hold the details of the callback endpoint
        EndpointReference from = null;
        if (callbackAddress != null ) {
            from = assemblyFactory.createEndpointReference();
            Endpoint fromEndpoint = assemblyFactory.createEndpoint();
            from.setTargetEndpoint(fromEndpoint);
            from.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
            msg.setFrom(from);
            Endpoint callbackEndpoint = assemblyFactory.createEndpoint();
            callbackEndpoint.setURI(callbackAddress);
            callbackEndpoint.setUnresolved(true);
            from.setCallbackEndpoint(callbackEndpoint);
        }

        Message response = endpoint.invoke(msg);
        
        if(response.isFault()) {
            throw new InvocationTargetException((Throwable) response.getBody());
        }
        return response.getBody();
    }
}
