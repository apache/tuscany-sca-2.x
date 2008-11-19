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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.util.PolicyHandler;

/**
 * Axis2OneWayBindingInvoker uses an Axis2 OperationClient to invoke a OneWay remote web service.
 *
 * @version $Rev$ $Date$
 */
public class Axis2OneWayBindingInvoker extends Axis2BindingInvoker {

    public Axis2OneWayBindingInvoker(Axis2ServiceClient serviceClient,
                                     QName wsdlOperationName,
                                     Options options,
                                     SOAPFactory soapFactory,
                                     List<PolicyHandler> policyHandlerList,
                                     WebServiceBinding wsBinding) {

        super(serviceClient, wsdlOperationName, options, soapFactory, policyHandlerList, wsBinding);
    }

    @Override
    protected Object invokeTarget(Message msg) throws AxisFault {
        OperationClient operationClient = createOperationClient(msg);

        // ensure connections are tracked so that they can be closed by the reference binding
        MessageContext requestMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        //requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        Options opt = requestMC.getOptions();
        opt.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        opt.setUseSeparateListener(true);
        opt.setProperty(HTTPConstants.AUTO_RELEASE_CONNECTION,Boolean.TRUE);        

        operationClient.execute(false);

        // REVIEW it seems ok to return null
        return null;
    }

}
