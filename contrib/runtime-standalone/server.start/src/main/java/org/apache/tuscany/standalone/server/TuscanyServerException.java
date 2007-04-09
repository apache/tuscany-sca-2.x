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
package org.apache.tuscany.standalone.server;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * Exception thrown by the tuscany server during startup and shutdown.
 * 
 * @version $Revisiion$ $Date$
 *
 */

@SuppressWarnings("serial")
public class TuscanyServerException extends TuscanyRuntimeException {

    /**
     * Initializes the cause.
     * @param cause Root cause of the exception.
     */
    public TuscanyServerException(Throwable cause) {
        super(cause);
    }

    /**
     * Initializes the message.
     * @param message Message of the exception.
     */
    public TuscanyServerException(String message) {
        super(message);
    }

}
