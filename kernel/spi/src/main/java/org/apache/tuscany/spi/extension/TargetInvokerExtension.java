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
package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The default implementation of a TargetInvoker
 *
 * @version $Rev$ $Date$
 */
public abstract class TargetInvokerExtension implements TargetInvoker {
    protected WorkContext workContext;
    protected boolean cacheable;

    /**
     * Creates a new invoker
     *
     * @param workContext the work context to use for setting correlation information
     */
    public TargetInvokerExtension(WorkContext workContext) {
        this.workContext = workContext;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object messageId = msg.getMessageId();
            WorkContext workContext = msg.getWorkContext();
            if (messageId != null) {
                workContext.setCorrelationId(messageId);
            }
            LinkedList<URI> callbackRoutingChain = msg.getCallbackUris();
            if (callbackRoutingChain != null) {
                workContext.setCallbackUris(callbackRoutingChain);
            }
            Object resp = invokeTarget(msg.getBody(), msg.getConversationSequence(), workContext);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable();
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // TargetInvoker extends Cloneable so this should not have been thrown
            throw new AssertionError(e);
        }
    }

}
