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
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

public class Axis2AsyncTargetInvoker extends Axis2TargetInvoker {

    protected static final Message RESPONSE = new ImmutableMessage();

    private InboundWire wire;
    private Object messageId;
    private Axis2ReferenceCallbackTargetInvoker callbackInvoker;

    public Axis2AsyncTargetInvoker(ServiceClient serviceClient,
            QName wsdlOperationName,
            Options options,
            SDODataBinding dataBinding,
            SOAPFactory soapFactory,
            InboundWire wire) {
        super(serviceClient, wsdlOperationName, options, dataBinding, soapFactory);
        this.wire = wire;
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        try {
            Object[] args = (Object[]) payload;
            boolean pureOMelement = (args != null && args.length > 0 && (args[0] instanceof OMElement));
            OperationClient operationClient = createOperationClient(args);
            callbackInvoker.setCorrelationId(messageId);
            Axis2ReferenceCallback callback =
                    new Axis2ReferenceCallback(callbackInvoker, getDataBinding(), pureOMelement);
            operationClient.setCallback(callback);

            operationClient.execute(false);

            return RESPONSE;
        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            wire.addMapping(msg.getMessageId(), msg.getFromAddress());
            messageId = msg.getMessageId();
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }
    
    public void setCallbackTargetInvoker(Axis2ReferenceCallbackTargetInvoker callbackInvoker) {
        this.callbackInvoker = callbackInvoker;
    }

    /**
     * A dummy message passed back on an invocation
     */
    private static class ImmutableMessage implements Message {

        public Object getBody() {
            return null;
        }

        public void setBody(Object body) {
            throw new UnsupportedOperationException();
        }

        public void setTargetInvoker(TargetInvoker invoker) {
            throw new UnsupportedOperationException();
        }

        public TargetInvoker getTargetInvoker() {
            return null;
        }

        public Message getRelatedCallbackMessage() {
            return null;
        }

        public Object getFromAddress() {
            return null;
        }

        public void setFromAddress(Object fromAddress) {
            throw new UnsupportedOperationException();
        }

        public Object getMessageId() {
            return null;
        }

        public void setMessageId(Object messageId) {
            throw new UnsupportedOperationException();
        }

        public Object getCorrelationId() {
            return null;
        }

        public void setCorrelationId(Object correlationId) {
            throw new UnsupportedOperationException();
        }
        
        public boolean isFault() {
            return false;
        }

        public void setBodyWithFault(Object fault) {
            throw new UnsupportedOperationException();
        }
        
    }
}
