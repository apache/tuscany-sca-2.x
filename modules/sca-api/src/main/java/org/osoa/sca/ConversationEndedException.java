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
 * Exception thrown to indicate the conversation being used for a stateful interaction has been ended.
 *
 * @version $Rev$ $Date$
 */
public class ConversationEndedException extends ServiceRuntimeException {
    private static final long serialVersionUID = 3734864942222558406L;

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @see ServiceRuntimeException
     */
    public ConversationEndedException() {
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message) {
        super(message);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @param cause   passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param cause passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(Throwable cause) {
        super(cause);
    }
}
