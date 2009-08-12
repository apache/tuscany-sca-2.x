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

package org.apache.tuscany.sca.implementation.osgi.xml;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * 
 */
public class ServiceDescriptionsModelResolver implements ModelResolver {
    private ServiceDescriptions serviceDescriptions;

    public ServiceDescriptionsModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories, Monitor monitor) {
        ServiceDescriptionsFactory factory = modelFactories.getFactory(ServiceDescriptionsFactory.class);
        this.serviceDescriptions = factory.createServiceDescriptions();
    }

    public void addModel(Object resolved) {
        // Merge the service descriptions
        if (resolved instanceof ServiceDescriptions) {
            serviceDescriptions.addAll((ServiceDescriptions)resolved);
        }
    }

    public Object removeModel(Object resolved) {
        // Remove the service descriptions
        if (resolved instanceof ServiceDescriptions) {
            serviceDescriptions.removeAll((ServiceDescriptions)resolved);
        }
        return resolved;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        // Always return the aggregated service descriptions
        return modelClass.cast(serviceDescriptions);
    }
}
