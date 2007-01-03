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
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory<T> implements ObjectFactory<T> {
    private Class<T> interfaze;
    private OutboundWire wire;
    private WireService wireService;
    // the cache of proxy interface method to operation mappings
    private Map<Method, OutboundChainHolder> mappings;
    //private boolean optimizable;

    /**
     * Constructor.
     *
     * @param interfaze   the interface to inject on the client
     * @param wire        the backing wire
     * @param wireService the wire service to create the proxy
     * @throws NoMethodForOperationException
     */
    public WireObjectFactory(Class<T> interfaze, OutboundWire wire, WireService wireService)
        throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.wire = wire;
        this.wireService = wireService;
        this.mappings = WireUtils.createInterfaceToWireMapping(interfaze, wire);
    }

    public T getInstance() throws ObjectCreationException {
// JFM TODO enable
//        if (optimizable || wire.isOptimizable()) {
//            optimizable = true;
//            try {
//                return interfaze.cast(wire.getTargetService());
//            } catch (TargetResolutionException e) {
//                throw new ObjectCreationException(e);
//            }
//        }
        // clone the cached mappings
        Map<Method, OutboundChainHolder> newChains = new HashMap<Method, OutboundChainHolder>(mappings.size());
        for (Map.Entry<Method, OutboundChainHolder> entry : mappings.entrySet()) {
            newChains.put(entry.getKey(), entry.getValue().clone());
        }
        return interfaze.cast(wireService.createProxy(interfaze, wire, newChains));
    }


}
