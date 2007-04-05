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
package org.apache.tuscany.spi.extension;

import java.net.URI;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * An extension point for atomic component type, which new implementation types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicComponentExtension extends AbstractComponentExtension implements AtomicComponent {
    protected ScopeContainer scopeContainer;
    protected Scope scope;
    protected ProxyService proxyService;
    protected WorkContext workContext;
    protected final URI groupId;
    private final int initLevel;
    private final long maxIdleTime;
    private final long maxAge;

    protected AtomicComponentExtension(URI name,
                                       ProxyService proxyService,
                                       WorkContext workContext,
                                       URI groupId,
                                       int initLevel) {
        this(name, proxyService, workContext, groupId, initLevel, -1, -1);
    }

    protected AtomicComponentExtension(URI name,
                                       ProxyService proxyService,
                                       WorkContext workContext,
                                       URI groupId,
                                       int initLevel,
                                       long maxIdleTime,
                                       long maxAge) {
        super(name);
        assert groupId != null;
        assert !(maxIdleTime > 0 && maxAge > 0);
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.groupId = groupId;
        this.initLevel = initLevel;
        this.maxIdleTime = maxIdleTime;
        this.maxAge = maxAge;
    }

    public Scope getScope() {
        return scope;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
        scope = scopeContainer.getScope();
    }

    public void start() throws CoreRuntimeException {
        super.start();
        scopeContainer.register(this, groupId);
    }

    public void stop() {
        scopeContainer.unregister(this);
        super.stop();
    }

    public void removeInstance() throws ComponentException {
        scopeContainer.remove(this);
    }

    public ObjectFactory createObjectFactory() {
        // FIXME: Is it the correct way to create an ObjectFactory for itself?
        return new ObjectFactory() {
            public Object getInstance() throws ObjectCreationException {
                try {
                    return scopeContainer.getWrapper(AtomicComponentExtension.this, groupId).getInstance();
                } catch (TargetResolutionException e) {
                    throw new ObjectCreationException(e);
                }
            }
        };
        // throw new UnsupportedOperationException();
    }
}
