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
package org.apache.tuscany.sca.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Class for tunnelling a WorkContext through the invocation of a user class.
 *
 * @version $Rev$ $Date$
 */
public final class ThreadMessageContext {
    
        // TUSCANY-3770: Used as a marker for detecting when thread context information can be removed
        private static final Message msg = new Message() {
        private Map<String, Object> headers = new HashMap<String, Object>();
        public void setTo(Endpoint to) {}
        public void setOperation(Operation op) {}
        public void setMessageID(Object messageId) {}
        public void setFrom(EndpointReference from) {}
        public <T> void setFaultBody(T fault) {}
        public <T> void setBody(T body) {}
        public <T> void setBindingContext(T bindingContext) {}
        public boolean isFault() {
            return false;
        }
        public Endpoint getTo() {
            return null;
        }
        public Operation getOperation() {
            return null;
        }
        public Object getMessageID() {
            return null;
        }
        public Map<String, Object> getHeaders() {
            return headers;
        }
        public EndpointReference getFrom() {
            return null;
        }
        public <T> T getBody() {
            return null;
        }
        public <T> T getBindingContext() {
            return null;
        }
    };

    private static final ThreadLocal<Message> CONTEXT = new ThreadLocal<Message>(){
        @Override
        protected synchronized Message initialValue() {
            return msg;
        }
    };
    
    private static final ThreadLocal<Message> PREVIOUS_CONTEXT = new ThreadLocal<Message>();

    private ThreadMessageContext() {
    }

    /**
     * Set the WorkContext for the current thread.
     * The current work context is returned and must be restored after the invocation is complete.
     * Typical usage would be:
     * <pre>
     *   WorkContext old = PojoWorkContextTunnel.setThreadWorkContext(newContext);
     *   try {
     *      ... invoke user code ...
     *   } finally {
     *     PojoWorkContextTunnel.setThreadWorkContext(old);
     *   }
     * </pre>
     * @param context
     * @return the current work context for the thread; this must be restored after the invocation is made
     */
    public static Message setMessageContext(Message context) {
        Message old = CONTEXT.get();
        CONTEXT.set(context);
        PREVIOUS_CONTEXT.set(old);
        
        // TUSCANY-3770: Remove thread context information when the request invocation has completed
        if (context == msg) {
            removeMessageContext();
            removePreviousMessageContext();
        }
        return old;
    }

    /**
     * Returns the WorkContext for the current thread.
     *
     * @return the WorkContext for the current thread
     */
    public static Message getMessageContext() {
        return CONTEXT.get();
    }
    
    public static Message getPreviousMessageContext() {
        return PREVIOUS_CONTEXT.get();
    }

    /**
     * Removes any state from the current thread to ensure that
     * any associated classloaders can be GCd
     */
    // TUSCANY-3770: The thread context information is removed implicitly above    
    public static void removeMessageContext() {
        CONTEXT.remove();
    }

    //for performance concerns - set to null rather than call remove
    public static void removePreviousMessageContext() {
        PREVIOUS_CONTEXT.set(null);
    }
}
