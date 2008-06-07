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

package org.apache.tuscany.sca.endpointresolver;

import java.util.List;


/**
 * An extension point for resolver factories. Holds all of the resolver
 * factories from loaded extension points. Allows a resolver factory
 * to be located based on a given model type. Hence the runtime can 
 * generate endpoint resolvers from the in memory assembly model. 
 *
 * @version $Rev$ $Date$
 */
public interface EndpointResolverFactoryExtensionPoint {


    /**
     * Add an endpoint resolver factory.
     * 
     * @param endpointResolverFactory The resolver factory
     */
    void addEndpointResolverFactory(EndpointResolverFactory endpointResolverFactory);

    /**
     * Remove a endpoint resolver factory.
     * 
     * @param endpointResolverFactory The endpoint resolver factory
     */
    void removeEndpointResolverFactory(EndpointResolverFactory endpointResolverFactory);

    /**
     * Returns the endpoint resolver factory associated with the given binding type.
     * @param bindingType A binding type
     * @return The endpoint resolver factory associated with the given binding type
     */
    EndpointResolverFactory getEndpointResolverFactory(Class<?> bindingType);
    
}
