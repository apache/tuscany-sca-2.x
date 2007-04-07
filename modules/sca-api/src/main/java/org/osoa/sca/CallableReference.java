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
package org.osoa.sca;

/**
 * Common superclass for references that can be passed between components.
 * 
 * @version $Rev$ $Date$
 * @param <B> the Java interface associated with this reference
 */
public interface CallableReference<B> {
    /**
     * Returns a type-safe reference to the target of this reference.
     * The instance returned is guaranteed to implement the business interface for this reference
     * but may not be a proxy as defined by java.lang.reflect.Proxy.
     *
     * @return a proxy to the target that implements the business interface associated with this reference
     */
    B getService();

    /**
     * Returns the Java class for the business interface associated with this reference.
     *
     * @return the Class for the business interface associated with this reference
     */
    Class<B> getBusinessInterface();

    /**
     * Returns true if this reference is conversational.
     *
     * @return true if this reference is conversational
     */
    boolean isConversational();

    /**
     * Returns the conversation associated with this reference.
     * Returns null if no conversation is currently active.
     *
     * @return the conversation associated with this reference; may be null
     */
    Conversation getConversation();

    /**
     * Returns the callback ID.
     *
     * @return the callback ID
     */
    Object getCallbackID();
}
