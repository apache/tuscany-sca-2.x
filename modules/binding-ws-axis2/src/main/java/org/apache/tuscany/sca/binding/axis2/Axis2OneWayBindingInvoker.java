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
package org.apache.tuscany.sca.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.invocation.Message;

public class Axis2OneWayBindingInvoker extends Axis2BindingInvoker {

    public Axis2OneWayBindingInvoker(ServiceClient serviceClient,
                                     QName wsdlOperationName,
                                     Options options,
                                     SOAPFactory soapFactory) {

        super(serviceClient, wsdlOperationName, options, soapFactory);
    }

    @Override
    protected Object invokeTarget(Message msg) throws InvocationTargetException {
        try {
            OperationClient operationClient = createOperationClient(msg);

            // ensure connections are tracked so that they can be closed by the reference binding
            MessageContext requestMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
            requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);

            operationClient.execute(false);

            // REVIEW it seems ok to return null
            return null;

        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }
    }

}
