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
 * Exception used to indicate that a runtime exception occurred during the invocation of and external service.
 *
 * @version $Rev$ $Date$
 */
public class ServiceUnavailableException extends ServiceRuntimeException {

    private static final long serialVersionUID = -5869397223249401047L;

    /**
     * Constructs a new ServiceUnavailableException.
     */
    public ServiceUnavailableException() {
        super((Throwable) null);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified detail message.
     *
     * @param message The detail message (which is saved to later retrieval by the getMessage() method).
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified cause.
     *
     * @param cause The cause (which is saved to later retrieval by the getCause() method).
     */
    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ServiceUnavailableException with the specified detail message and cause.
     *
     * @param message The message (which is saved to later retrieval by the getMessage() method).
     * @param cause   The cause (which is saved to later retrieval by the getCause() method).
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

}
