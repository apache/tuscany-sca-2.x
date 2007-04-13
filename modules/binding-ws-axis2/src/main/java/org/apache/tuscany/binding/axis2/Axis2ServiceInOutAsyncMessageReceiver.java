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

import java.util.concurrent.CountDownLatch;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.wire.MessageId;

public class Axis2ServiceInOutAsyncMessageReceiver extends AbstractMessageReceiver {

    private Operation operation;

    private Axis2ServiceBinding service;

    public Axis2ServiceInOutAsyncMessageReceiver(Axis2ServiceBinding service,
                                                 Operation operation) {
        this.operation = operation;
        this.service = service;
    }

    public Axis2ServiceInOutAsyncMessageReceiver() {
    }

    public final void receive(final MessageContext messageCtx) {
        try {
            Object messageId = messageCtx.getMessageID();
            if (messageId == null) {
                messageId = new MessageId();
            }

            // Now use message id as index to context to be used by callback
            // target invoker
            CountDownLatch doneSignal = new CountDownLatch(1);
//            InvocationContext invCtx =
//                service.new InvocationContext(messageCtx, operation, getSOAPFactory(messageCtx), doneSignal);
//            service.addMapping(messageId, invCtx);

            invokeBusinessLogic(messageCtx, messageId);
            
            try {
                doneSignal.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        } catch (AxisFault e) {
            // log.error(e);
        }
    }

    private void invokeBusinessLogic(MessageContext inMC, Object messageId) throws AxisFault {
        try {
            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Object[] args = new Object[] {requestOM};
            String conversationID = service.isConversational() ?  Axis2ServiceBinding.getConversationID(inMC) : null;
//            service.invokeTarget(operation, args, messageId, conversationID);
//        } catch (InvocationTargetException e) {
//            Throwable t = e.getCause();
//            if (t instanceof Exception) {
//                throw AxisFault.makeFault((Exception)t);
//            }
//            throw new InvocationRuntimeException(e);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

    }
}
