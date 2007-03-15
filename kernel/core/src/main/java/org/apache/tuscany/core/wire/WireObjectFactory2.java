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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory2<T> implements ObjectFactory<T> {
    private Class<T> interfaze;
    private boolean conversational;
    private Wire wire;
    private ProxyService proxyService;
    // the cache of proxy interface method to operation mappings
    private Map<Method, InvocationChain> mappings;

    /**
     * Constructor.
     *
     * @param interfaze      the interface to inject on the client
     * @param conversational if the wire is conversational
     * @param wire           the backing wire
     * @param proxyService   the wire service to create the proxy
     * @throws NoMethodForOperationException if a method matching the operation cannot be found
     */
    public WireObjectFactory2(Class<T> interfaze, boolean conversational, Wire wire, ProxyService proxyService)
        throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.conversational = conversational;
        this.wire = wire;
        this.proxyService = proxyService;
        this.mappings = WireUtils.createInterfaceToWireMapping2(interfaze, wire);
    }

    public T getInstance() throws ObjectCreationException {
        return interfaze.cast(proxyService.createProxy2(interfaze, conversational, wire));
    }
}

