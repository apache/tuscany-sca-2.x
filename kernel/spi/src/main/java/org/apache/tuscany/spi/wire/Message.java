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
package org.apache.tuscany.spi.wire;

/**
 * Represents a request, response, or exception flowing through a wire
 *
 * @version $Rev $Date
 */
public interface Message {

    /**
     * Returns the body of the message, which will be the payload or parameters associated with the wire
     */
    Object getBody();

    /**
     * Sets the body of the message.
     */
    void setBody(Object body);

    /**
     * Sets the target invoker to dispatch to when the message passes through the request side of the invocation chain
     */
    void setTargetInvoker(TargetInvoker invoker);

    /**
     * Sets the target invoker to dispatch to when the message passes through the request side of the invocation chain
     */
    TargetInvoker getTargetInvoker();

    /**
     * Returns the callback channel
     */
    MessageChannel getCallbackChannel();

    /**
     * 
     */
    Message getRelatedCallbackMessage();
}
