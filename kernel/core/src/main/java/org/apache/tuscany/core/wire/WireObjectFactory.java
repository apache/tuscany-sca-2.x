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
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * Uses a wire to return an object instance
 * @Deprecated 
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory<T> implements ObjectFactory<T> {
    private Class<T> interfaze;
    private Wire wire;
    private ProxyService proxyService;
    // the cache of proxy interface method to operation mappings
    private Map<Method, ChainHolder> mappings;
    private boolean optimizable;

    /**
     * Constructor.
     *
     * @param interfaze   the interface to inject on the client
     * @param wire        the backing wire
     * @param proxyService the wire service to create the proxy
     * @throws NoMethodForOperationException
     */
    public WireObjectFactory(Class<T> interfaze, Wire wire, ProxyService proxyService)
        throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.wire = wire;
        this.proxyService = proxyService;
        this.mappings = WireUtils.createInterfaceToWireMapping(interfaze, wire);
        if (wire.isOptimizable()
            && wire.getSourceContract().getInterfaceClass() != null
            && interfaze.isAssignableFrom(wire.getSourceContract().getInterfaceClass())) {
            optimizable = true;
        }
    }

    public T getInstance() throws ObjectCreationException {
        if (optimizable) {
            try {
                return interfaze.cast(wire.getTargetInstance());
            } catch (TargetResolutionException e) {
                throw new ObjectCreationException(e);
            }
        } else {
            // clone the cached mappings
            Map<Method, ChainHolder> newChains = new HashMap<Method, ChainHolder>(mappings.size());
            for (Map.Entry<Method, ChainHolder> entry : mappings.entrySet()) {
                newChains.put(entry.getKey(), entry.getValue().clone());
            }
            return interfaze.cast(proxyService.createProxy(interfaze, wire, newChains));
        }
    }


}
