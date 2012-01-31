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

package org.apache.tuscany.sca.client.impl;

import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.oasisopen.sca.NoSuchServiceException;

public class DefaultEndpointFinder implements EndpointFinder {

    protected boolean onlySCABinding;

    @Override
    public Endpoint findEndpoint(DomainRegistry domainRegistry, String serviceName) throws NoSuchServiceException {
        List<Endpoint> eps = domainRegistry.findEndpoint(serviceName);
        if (eps == null || eps.size() < 1) {
            throw new NoSuchServiceException(serviceName);
        }
        
        // If there is an Endpoint using the SCA binding use that
        for (Endpoint ep : eps) {
            if (SCABinding.TYPE.equals(ep.getBinding().getType())) {
                return ep;
            }
        }
        
        if (onlySCABinding) {
            throw new NoSuchServiceException(serviceName + " not found using binding.sca");
        }
        
        // Otherwise just choose the first one
        return eps.get(0);
    }
}
