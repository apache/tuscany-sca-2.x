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
package org.apache.tuscany.core.component;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.implementation.composite.ComponentTimeoutException;

/**
 * The standard implementation of a composite component. Autowiring is performed by delegating to the parent composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentImpl extends CompositeComponentExtension {
    public static final int DEFAULT_WAIT = 1000 * 60;
    // Blocking latch to ensure the composite is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);
    protected final Object lock = new Object();
    // Indicates whether the composite context has been initialized
    protected boolean initialized;

    /**
     * Constructor
     *
     * @param name the name of this Component
     */
    public CompositeComponentImpl(URI name) {
        super(name);
    }

    public void attachWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
    }

    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }
    
    public void configureProperty(String propertyName) {
        //throw new UnsupportedOperationException();
        
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("Composite not in UNINITIALIZED state");
            }
            initializeLatch.countDown();
            initialized = true;
            lifecycleState = INITIALIZED;
        }
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }

        publish(new ComponentStop(this, getUri()));
        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;
    }

    public void publish(Event event) {
        if (lifecycleState == STOPPED) {
            return;
        }
        checkInit();
        super.publish(event);
    }

    /**
     * Blocks until the composite context has been initialized
     */
    protected void checkInit() throws ComponentTimeoutException {
        if (!initialized) {
            try {
                /* block until the composite has initialized */
                boolean success = initializeLatch.await(DEFAULT_WAIT, TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ComponentTimeoutException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }

}
