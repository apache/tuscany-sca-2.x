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
package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Implementations are responsible for resolving a target and performing the actual invocation on it, for example, a
 * component implementation instance or a service client.
 *
 * @version $Rev$ $Date$
 * @Deprecated
 */
public interface TargetInvoker extends Cloneable {

    /**
     * Invokes an operation on a target with the given payload. Used in optmized cases where messages do not need to be
     * flowed such as in non-proxied wires.
     *
     * @param payload     the invocation payload, typically an array of parameters
     * @param sequence    if the invocation is part of a conversation, the sequence. Valid values are {@link #NONE} for
     *                    non-conversational, {@link #START} to begin a conversation, {@link #CONTINUE} to continue a
     *                    conversation, or {@link #END} to end a conversation
     * @return the result of the invocation
     * @throws InvocationTargetException if there was a problem invoking the target
     */
    Object invokeTarget(final Object payload, final ConversationSequence sequence) throws InvocationTargetException;

    /**
     * Invokes an operation on a target with the given message
     */
    Message invoke(Message msg);

    /**
     * Determines whether the proxy can be cached on the client/source side
     */
    boolean isCacheable();

    /**
     * Sets whether the target service instance may be cached by the invoker. This is a possible optimization when a
     * wire is configured for a "down-scope" reference, i.e. a reference from a source of a shorter lifetime to a source
     * of greater lifetime.
     */
    void setCacheable(boolean cacheable);

    /**
     * Implementations must support deep cloning
     */
    Object clone() throws CloneNotSupportedException;
}
