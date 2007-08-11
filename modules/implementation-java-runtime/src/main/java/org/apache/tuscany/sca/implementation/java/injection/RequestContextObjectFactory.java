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
package org.apache.tuscany.sca.implementation.java.injection;

import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.component.RequestContextImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.factory.ObjectCreationException;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.osoa.sca.RequestContext;

/**
 * Creates instances of
 * {@link org.apache.tuscany.sca.core.component.RequestContextImpl} for
 * injection on component implementation instances
 * 
 * @version $Rev$ $Date$
 */
public class RequestContextObjectFactory implements ObjectFactory<RequestContext> {
    private RequestContextFactory factory;
    private ProxyFactory proxyService;

    public RequestContextObjectFactory(RequestContextFactory factory) {
        this(factory, null);
    }

    public RequestContextObjectFactory(RequestContextFactory factory, ProxyFactory proxyService) {
        this.factory = factory;
        this.proxyService = proxyService;
    }

    public RequestContext getInstance() throws ObjectCreationException {
        if (factory != null) {
            return factory.createRequestContext();
        } else {
            return new RequestContextImpl(proxyService);
        }
    }
}
