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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.tuscany.binding.axis2.Axis2Service.InvocationContext;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.MessageId;

public class Axis2ServiceInOutAsyncMessageReceiver extends AbstractMessageReceiver {

    private Operation<?> operation;

    private WorkContext workContext;

    private Axis2Service service;

    public Axis2ServiceInOutAsyncMessageReceiver(Axis2Service service,
                                                 Operation operation,
                                                 WorkContext workContext) {
        this.operation = operation;
        this.workContext = workContext;
        this.service = service;
    }

    public Axis2ServiceInOutAsyncMessageReceiver() {
    }

    /*
     * @Override public void invokeBusinessLogic(MessageContext inMC,
     * MessageContext outMC) throws AxisFault { try { // Create a new message id
     * and hand it to // JDKInboundInvocationHandler // via work context
     * MessageId messageId = new MessageId();
     * workContext.setCurrentMessageId(messageId); // Now use message id as
     * index to context to be used by callback // target invoker
     * InvocationContext invCtx = service.new InvocationContext(inMC, operation,
     * getSOAPFactory(inMC)); service.addMapping(messageId, invCtx); OMElement
     * requestOM = inMC.getEnvelope().getBody().getFirstElement(); Object[] args =
     * new Object[] {requestOM}; // FIXME: It seems that the AsyncTargetInvoker
     * always returns null OMElement responseOM =
     * (OMElement)service.invokeTarget(operation, args); SOAPEnvelope
     * soapEnvelope = getSOAPFactory(inMC).getDefaultEnvelope(); if (responseOM !=
     * null) { soapEnvelope.getBody().addChild(responseOM); }
     * outMC.setEnvelope(soapEnvelope);
     * outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN,
     * Constants.VALUE_TRUE); } catch (InvocationTargetException e) { Throwable
     * t = e.getCause(); if (t instanceof Exception) { throw
     * AxisFault.makeFault((Exception)t); } throw new
     * InvocationRuntimeException(e); } catch (Exception e) { throw
     * AxisFault.makeFault(e); } }
     */

    public final void receive(final MessageContext messageCtx) {
        try {
            // Create a new message id and hand it to
            // JDKInboundInvocationHandler
            // via work context
            MessageId messageId = new MessageId();
            workContext.setCurrentMessageId(messageId);
            // Now use message id as index to context to be used by callback
            // target invoker
            InvocationContext invCtx =
                service.new InvocationContext(messageCtx, operation, getSOAPFactory(messageCtx));
            service.addMapping(messageId, invCtx);

            invokeBusinessLogic(messageCtx);
        } catch (AxisFault e) {
            // log.error(e);
        }
    }

    public void invokeBusinessLogic(MessageContext inMC) throws AxisFault {
        try {
            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Object[] args = new Object[] {requestOM};
            service.invokeTarget(operation, args);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof Exception) {
                throw AxisFault.makeFault((Exception)t);
            }
            throw new InvocationRuntimeException(e);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

    }
}
