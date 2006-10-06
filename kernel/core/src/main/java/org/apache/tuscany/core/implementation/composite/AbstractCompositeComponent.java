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
package org.apache.tuscany.core.implementation.composite;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ObjectRegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.component.ComponentInitException;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.implementation.system.component.SystemSingletonAtomicComponent;

/**
 * The base implementation of a composite context
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeComponent extends CompositeComponentExtension {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    protected final Object lock = new Object();

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    protected ScopeContainer scopeContainer;


    /**
     * @param name           the name of the SCA composite
     * @param parent         the SCA composite parent
     * @param connector
     * @param propertyValues the values of this composite's Properties
     */
    public AbstractCompositeComponent(String name,
                                      CompositeComponent parent,
                                      Connector connector,
                                      Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        assert this.scopeContainer == null;
        this.scopeContainer = scopeContainer;
        addListener(scopeContainer);
    }

    public <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ObjectRegistrationException {
        register(new SystemSingletonAtomicComponent<S, I>(name, this, service, instance));
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("Composite not in UNINITIALIZED state");
            }

            if (scopeContainer != null) {
                scopeContainer.start();
            }
            for (SCAObject child : systemChildren.values()) {
                child.start();
            }
            for (SCAObject child : children.values()) {
                child.start();
            }
            initializeLatch.countDown();
            initialized = true;
            lifecycleState = INITIALIZED;
        }
        publish(new CompositeStart(this, this));
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }

        publish(new CompositeStop(this, this));
        for (SCAObject child : children.values()) {
            child.stop();
        }
        for (SCAObject child : systemChildren.values()) {
            child.stop();
        }
        if (scopeContainer != null) {
            scopeContainer.stop();
        }

        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;
    }

    public void publish(Event event) {
        if (lifecycleState == STOPPED){
            return;
        }
        checkInit();
        super.publish(event);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return null;
    }

    public TargetInvoker createAsyncTargetInvoker(InboundWire wire, Operation operation) {
        return null;
    }


    /**
     * Blocks until the module context has been initialized
     */
    protected void checkInit() {
        if (!initialized) {
            try {
                /* block until the module has initialized */
                boolean success = initializeLatch.await(AbstractCompositeComponent.DEFAULT_WAIT,
                    TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ComponentInitException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }


}
