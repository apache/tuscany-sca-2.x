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
package org.apache.tuscany.sca.scope;

/**
 * Implementations adhere to runtime lifecycle semantics
 *
 * @version $Rev$ $Date$
 */
public interface Lifecycle {
    /* A configuration error state */
    int CONFIG_ERROR = -1;
    /* Has not been initialized */
    int UNINITIALIZED = 0;
    /* In the process of being configured and initialized */
    int INITIALIZING = 1;
    /* Instantiated and configured */
    int INITIALIZED = 2;
    /* Configured and initialized */
    int RUNNING = 4;
    /* In the process of being shutdown */
    int STOPPING = 5;
    /* Has been shutdown and removed from the composite */
    int STOPPED = 6;
    /* In an error state */
    int ERROR = 7;

    /**
     * Returns the lifecycle state
     *
     * @see #UNINITIALIZED
     * @see #INITIALIZING
     * @see #INITIALIZED
     * @see #RUNNING
     * @see #STOPPING
     * @see #STOPPED
     */
    int getLifecycleState();

    /**
     * Starts the Lifecycle.
     *
     * @throws CoreRuntimeException
     */
    void start() throws CoreRuntimeException;

    /**
     * Stops the Lifecycle.
     *
     * @throws CoreRuntimeException
     */
    void stop() throws CoreRuntimeException;
}
