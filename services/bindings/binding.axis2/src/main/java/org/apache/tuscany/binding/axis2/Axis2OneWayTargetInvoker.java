/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

public class Axis2OneWayTargetInvoker extends Axis2TargetInvoker {

    protected static final OMElement RESPONSE = null;

    public Axis2OneWayTargetInvoker(ServiceClient serviceClient,
                                    QName wsdlOperationName,
                                    Options options,
                                    SOAPFactory soapFactory) {

        super(serviceClient, wsdlOperationName, options, soapFactory);
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        try {
            Object[] args = (Object[])payload;
            OperationClient operationClient = createOperationClient(args);

            operationClient.execute(false);

            // REVIEW it seems ok to return null
            return RESPONSE;
        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        } catch (Throwable t) {
            throw new Axis2BindingRunTimeException(t);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }
}
