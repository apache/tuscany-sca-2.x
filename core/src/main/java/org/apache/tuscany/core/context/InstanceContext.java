/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.context;

/**
 * Manages instances of a runtime artifact. An <code>InstanceContext</code> may contain child contexts which
 * themselves manage implementation instances or it may be a leaf context.
 * 
 * @see org.apache.tuscany.core.context.SimpleComponentContext
 * @see org.apache.tuscany.core.context.AggregateContext
 * @see org.apache.tuscany.core.context.EntryPointContext
 * @see org.apache.tuscany.core.context.ExternalServiceContext
 * An entity that provides an execution context for a runtime artifact
 * Manages instances of a runtime artifact. An <code>InstanceContext</code> may contain child contexts which
 * themselves manage implementation instances or it may be a leaf context.
 *
 *
 * @version $Rev$ $Date$
 */
public interface InstanceContext{


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
         * Registers a listener to receive notifications for the context
         */
        public void addListener(RuntimeEventListener listener);

        /**
         * Removes a previously registered listener
         */
        public void removeListener(RuntimeEventListener listener);


        /**
         * Returns the instance associated with the requested name, which may be in a simple or compound form. Simple (i.e.
         * leaf) contexts will return an instance associated with the service name part of the compound name, which may be
         * null.
         * <p>
         * Aggregate contexts will return an instance (likely a proxy) of a contained entry point context. In this case, the
         * port name on the qualified name will correspond to the aggregate context name and the part name will be used to
         * retrieve the contained entry point context. The latter may be null. If the contained context is not an entry
         * point context, an exception will be thrown.
         *
         * @param qName a qualified name of the requested instance
         * @return the implementation instance or a proxy to it
         * @throws org.apache.tuscany.core.context.TargetException if an error occurs retrieving the instance or the requested component is not an entry
         *         point.
         *
         * @see org.apache.tuscany.core.context.AggregateContext
         * @see org.apache.tuscany.model.assembly.EntryPoint
         */
        Object getInstance(QualifiedName qName) throws TargetException;

}
