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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.MalformedNameException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;

/**
 * The base implementation of a composite context
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeComponent extends CompositeComponentExtension {
    public static final int DEFAULT_WAIT = 1000 * 60;
    // Blocking latch to ensure the composite is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);
    protected final Object lock = new Object();
    // Indicates whether the composite context has been initialized
    protected boolean initialized;


    /**
     * @param name           the name of the SCA composite
     * @param parent         the SCA composite parent
     * @param connector      the connector for fusing wires
     * @param propertyValues the values of this composite's Properties
     */
    public AbstractCompositeComponent(URI name,
                                      CompositeComponent parent,
                                      Connector connector,
                                      Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
    }

    public <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ComponentRegistrationException {
        URI uri;
        try {
            uri = new URI(name);
        } catch (URISyntaxException e) {
            throw new MalformedNameException(e);
        }
        register(new SystemSingletonAtomicComponent<S, I>(uri, this, service, instance));
    }

    public <S, I extends S> void registerJavaObject(String name, List<Class<?>> services, I instance)
        throws ComponentRegistrationException {
        URI uri;
        try {
            uri = new URI(name);
        } catch (URISyntaxException e) {
            throw new MalformedNameException(e);
        }
        register(new SystemSingletonAtomicComponent<S, I>(uri, this, services, instance));
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("Composite not in UNINITIALIZED state");
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
        publish(new ComponentStart(this, getUri()));
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }

        for (SCAObject child : children.values()) {
            child.stop();
        }
        for (SCAObject child : systemChildren.values()) {
            child.stop();
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

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
        throws TargetInvokerCreationException {
        return null;
    }

    /**
     * Blocks until the composite context has been initialized
     */
    protected void checkInit() throws ComponentTimeoutException {
        if (!initialized) {
            try {
                /* block until the composite has initialized */
                boolean success = initializeLatch.await(AbstractCompositeComponent.DEFAULT_WAIT,
                    TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ComponentTimeoutException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }


}
