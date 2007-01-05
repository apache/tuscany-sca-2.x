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

import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The default implementation of a {@link org.osoa.sca.CompositeContext} injected on a component implementation
 * instance
 *
 * @version $Rev$ $Date$
 */
public class ManagedCompositeContext extends AbstractCompositeContext {

    /**
     * Constructor.
     *
     * @param composite   the parent composite of the component whose instance the current context is injected on
     * @param wireService the wire service to use for generating proxies
     */
    public ManagedCompositeContext(final CompositeComponent composite, final WireService wireService) {
        super(composite, wireService);
    }

    public String getCompositeName() {
        return composite.getName();
    }

    public String getCompositeURI() {
        throw new UnsupportedOperationException();
    }

    public RequestContext getRequestContext() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference createServiceReferenceForSession(Object self, String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference newSession(String serviceName, Object sessionId) {
        throw new UnsupportedOperationException();
    }

    public void start() {
        throw new UnsupportedOperationException();
    }

    public void stop() {
        throw new UnsupportedOperationException();
    }
}
