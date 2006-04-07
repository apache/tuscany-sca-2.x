/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

/**
 * An entity that provides an execution context for a runtime artifact 
 * 
 * @version $Rev$ $Date$
 */
public interface Context {

    /* A configuration error state */
    public static final int CONFIG_ERROR = -1;

    /* Has not been initialized */
    public static final int UNINITIALIZED = 0;

    /* In the process of being configured and initialized */
    public static final int INITIALIZING = 1;

    /* Instantiated and configured */
    public static final int INITIALIZED = 2;

    /* Configured and initialized */
    public static final int RUNNING = 4;

    /* In the process of being shutdown */
    public static final int STOPPING = 5;

    /* Has been shutdown and removed from the module */
    public static final int STOPPED = 6;

    /* In an error state */
    public static final int ERROR = 7;

    /**
     * Returns the name of the context
     */
    public String getName();

    /**
     * Sets the name of the context
     */
    public void setName(String name);

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
    public int getLifecycleState();

    /**
     * Starts the container
     * 
     * @throws CoreRuntimeException
     */
    public void start() throws CoreRuntimeException;

    /**
     * Stops the container
     * 
     * @throws CoreRuntimeException
     */
    public void stop() throws CoreRuntimeException;

    /**
     * Registers a listener for context events
     */
    public void addContextListener(LifecycleEventListener listener);

    /**
     * Deregisters a context event listener
     */
    public void removeContextListener(LifecycleEventListener listener);

}

