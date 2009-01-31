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

package org.apache.tuscany.sca.policy.transaction;

import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * @version $Rev$ $Date$
 */
public class TransactionInterceptor implements Interceptor {
    private Invoker next;
    private TransactionManagerHelper helper;
    private boolean outbound;
    private TransactionPolicy interactionPolicy;
    private TransactionPolicy implementationPolicy;

    public TransactionInterceptor(TransactionManagerHelper helper,
                                  boolean outbound,
                                  TransactionPolicy interactionPolicy,
                                  TransactionPolicy implementationPolicy) {
        super();
        this.helper = helper;
        this.outbound = outbound;
        this.interactionPolicy = interactionPolicy;
        this.implementationPolicy = implementationPolicy;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#getNext()
     */
    public Invoker getNext() {
        return next;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#setNext(org.apache.tuscany.sca.invocation.Invoker)
     */
    public void setNext(Invoker next) {
        this.next = next;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {
        TransactionalInvocation invocation = new TransactionalInvocation(next, msg);

        Message result = null;
        if (msg.getOperation().isNonBlocking()) {

        }
        TransactionIntent interactionIntent = TransactionIntent.propagatesTransacton;
        if (interactionPolicy != null) {
            if (interactionPolicy.getAction() == TransactionPolicy.Action.PROPAGATE) {
                interactionIntent = TransactionIntent.propagatesTransacton;
            } else {
                interactionIntent = TransactionIntent.suspendsTransaction;
            }
        }
        TransactionIntent implementationIntent = TransactionIntent.managedTransactionGlobal;
        if (implementationPolicy != null) {
            switch (implementationPolicy.getAction()) {
                case REQUIRE_GLOBAL:
                    implementationIntent = TransactionIntent.managedTransactionGlobal;
                    break;
                case REQUIRE_LOCAL:
                    implementationIntent = TransactionIntent.managedTransactionLocal;
                    break;
                default:
                    implementationIntent = TransactionIntent.noManagedTransaction;
                    break;
            }
        }
        try {
            if (outbound) {
                result = helper.handlesOutbound(interactionIntent, implementationIntent, invocation);
            } else {
                result = helper.handlesInbound(interactionIntent, implementationIntent, invocation);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private static class TransactionalInvocation implements TransactionalAction<Message> {
        private final Invoker invoker;
        private final Message message;

        public TransactionalInvocation(Invoker invoker, Message message) {
            super();
            this.invoker = invoker;
            this.message = message;
        }

        public Message run() throws Exception {
            return invoker.invoke(message);
        }

    }

}
