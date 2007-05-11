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
package org.apache.tuscany.implementation.java.invocation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.spi.component.WorkContext;

/**
 * The default implementation of a TargetInvoker
 * 
 * @version $Rev$ $Date$
 */
public abstract class TargetInvokerExtension implements TargetInvoker {
    protected boolean cacheable;

    public Message invoke(Message msg) {
        try {
            Object messageId = msg.getMessageID();
            WorkContext workContext = msg.getWorkContext();
            if (messageId != null) {
                workContext.setCorrelationId(messageId);
            }
            Object resp = invokeTarget(msg.getBody(), msg.getConversationSequence(), workContext);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // TargetInvoker extends Cloneable so this should not have been
            // thrown
            throw new AssertionError(e);
        }
    }

    protected InvocationChain getInvocationChain(List<InvocationChain> chains, Operation targetOperation) {
        for (InvocationChain chain : chains) {
            if (chain.getTargetOperation().equals(targetOperation)) {
                return chain;
            }
        }
        return null;
    }

}
