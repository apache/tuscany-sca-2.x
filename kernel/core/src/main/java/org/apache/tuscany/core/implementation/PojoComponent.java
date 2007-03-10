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
package org.apache.tuscany.core.implementation;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Base class for Component implementations based on Java objects.
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class
 */
public abstract class PojoComponent<T> extends AbstractSCAObject implements AtomicComponent {
    private final InstanceFactoryProvider<T> provider;
    private final int initLevel;
    private final long maxIdleTime;
    private final long maxAge;
    private InstanceFactory<T> instanceFactory;

    public PojoComponent(URI componentId,
                         InstanceFactoryProvider<T> provider,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge) {
        super(componentId);
        this.provider = provider;
        this.initLevel = initLevel;
        this.maxIdleTime = maxIdleTime;
        this.maxAge = maxAge;
    }

    public void attachWire(Wire wire) {
        provider.attachWire(wire);
    }

    public void attachWires(List<Wire> wires) {
        provider.attachWires(wires);
    }

    public void start() {
        super.start();
        instanceFactory = provider.createFactory();
    }

    public void stop() {
        instanceFactory = null;
        super.stop();
    }

    public InstanceWrapper<T> createInstanceWrapper() {
        return instanceFactory.newInstance();
    }


    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }
}
