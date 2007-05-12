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

import java.util.concurrent.CountDownLatch;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.tuscany.interfacedef.Operation;

public class Axis2ServiceInOutAsyncMessageReceiver extends AbstractMessageReceiver {

    private Operation operation;

    private Axis2ServiceBindingProvider provider;

    public Axis2ServiceInOutAsyncMessageReceiver(Axis2ServiceBindingProvider provider,
                                                 Operation operation) {
        this.provider = provider;
        this.operation = operation;
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
            String conversationID = provider.isConversational() ?  Axis2ServiceBindingProvider.getConversationID(inMC) : null;
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

    /**
     * A unique identifier for a message flowing on a wire, potentially end-to-end (ie, through more than one SCAObject to
     * SCAObject hop).
     *
     * This used to be in the org.apache.tuscany.spi.wire package
     */
    public class MessageId {

        private long timestamp;

        public MessageId() {
            this.timestamp = System.currentTimeMillis();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String toString() {
            return "MsgId[" + timestamp + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final MessageId messageId = (MessageId) o;
            return timestamp == messageId.timestamp;
        }

        public int hashCode() {
            return (int) (timestamp ^ (timestamp >>> 32));
        }

    }

}
