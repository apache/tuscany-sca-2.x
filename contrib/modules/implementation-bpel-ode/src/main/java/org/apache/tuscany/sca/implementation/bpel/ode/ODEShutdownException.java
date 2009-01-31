/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.implementation.bpel.ode;

/**
 * Thrown when ODE failed to shutdown.
 * 
 * @version $Rev$ $Date$
 */
public class ODEShutdownException extends RuntimeException {
    private static final long serialVersionUID = -2869674556330744215L;

    public ODEShutdownException(Throwable cause) {
        super(cause);
    }

    public ODEShutdownException(String message) {
        super(message);
    }

    public ODEShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
