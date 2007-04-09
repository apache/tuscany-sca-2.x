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
package org.apache.tuscany.core.resolver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.resolver.ResolutionException;

/**
 * Default implementation of an autowire resolver
 *
 * @version $Rev$ $Date$
 */
public class DefaultAutowireResolver implements AutowireResolver {
    private Map<ServiceContract, URI> hostAutowire = new HashMap<ServiceContract, URI>();


    @SuppressWarnings({"unchecked"})
    public void resolve(ComponentDefinition<Implementation<CompositeComponentType<?, ?, ?>>> parentDefinition,
                        ComponentDefinition<? extends Implementation<?>> definition)
        throws ResolutionException {
        ComponentType<?, ?, ?> type = definition.getImplementation().getComponentType();
        // resolve autowires
        if (type instanceof CompositeComponentType) {
            CompositeComponentType<?, ?, ?> compositeType = (CompositeComponentType<?, ?, ?>) type;
            for (ComponentDefinition<? extends Implementation<?>> child : compositeType.getComponents().values()) {
                Implementation<?> implementation = child.getImplementation();
                ComponentType<?, ?, ?> childType = implementation.getComponentType();
                if (childType instanceof CompositeComponentType) {
                    // recurse decendents for composites
                    resolve((ComponentDefinition<Implementation<CompositeComponentType<?, ?, ?>>>) definition, child);
                }
                Map<String, ReferenceTarget> targets = child.getReferenceTargets();
                for (ReferenceDefinition reference : childType.getReferences().values()) {
                    ReferenceTarget target = targets.get(reference.getUri().getFragment());
                    if (target == null) {
                        continue;
                    }
                    if (target.isAutowire()) {
                        ServiceContract requiredContract = reference.getServiceContract();
                        resolve(compositeType, requiredContract, target, reference.isRequired());
                    }
                }
            }
        } else {
            // a leaf level component
            ComponentType<?, ?, ?> componentType = definition.getImplementation().getComponentType();
            Map<String, ReferenceTarget> targets = definition.getReferenceTargets();
            for (ReferenceDefinition reference : componentType.getReferences().values()) {
                ReferenceTarget target = targets.get(reference.getUri().getFragment());
                if (target == null) {
                    continue;
                }
                if (target.isAutowire()) {
                    ServiceContract requiredContract = reference.getServiceContract();
                    CompositeComponentType<?, ?, ?> ctype = parentDefinition.getImplementation().getComponentType();
                    resolve(ctype, requiredContract, target, reference.isRequired());
                }
            }
        }
    }

    public void addHostUri(ServiceContract contract, URI uri) {
        hostAutowire.put(contract, uri);
    }

    public void resolve(CompositeComponentType<?, ?, ?> compositeType) throws ResolutionException {
        for (ComponentDefinition<? extends Implementation<?>> child : compositeType.getComponents().values()) {
            Implementation<?> implementation = child.getImplementation();
            ComponentType<?, ?, ?> childType = implementation.getComponentType();
            if (childType instanceof CompositeComponentType) {
                // recurse decendents for composites
                resolve(null, child);
            }
            Map<String, ReferenceTarget> targets = child.getReferenceTargets();
            for (ReferenceDefinition reference : childType.getReferences().values()) {
                ReferenceTarget target = targets.get(reference.getUri().getFragment());
                if (target == null) {
                    continue;
                }
                if (target.isAutowire()) {
                    ServiceContract requiredContract = reference.getServiceContract();
                    resolve(compositeType, requiredContract, target, reference.isRequired());
                }
            }
        }
    }
    /**
     * Performs the actual resolution against a composite TODO this should be extensible allowing for path
     * optimizations
     *
     * @param compositeType    the composite component type to resolve against
     * @param requiredContract the required target contract
     * @param target           the reference target
     * @param required         true if the autowire is required
     * @throws AutowireTargetNotFoundException
     *
     */
    private void resolve(CompositeComponentType<?, ?, ?> compositeType,
                         ServiceContract requiredContract,
                         ReferenceTarget target,
                         boolean required) throws AutowireTargetNotFoundException {
        // for now, attempt to match on interface, assume the class can be loaded
        Class<?> requiredInterface = requiredContract.getInterfaceClass();
        if (requiredInterface == null) {
            throw new UnsupportedOperationException("Only interfaces support for autowire");
        }
        // autowire to a target in the parent
        URI targetUri = null;
        URI candidateUri = null;
        // find a suitable target, starting with components first
        for (ComponentDefinition<? extends Implementation<?>> candidate : compositeType.getComponents().values()) {
            Implementation<?> candidateImpl = candidate.getImplementation();
            ComponentType<?, ?, ?> candidateType = candidateImpl.getComponentType();
            for (ServiceDefinition service : candidateType.getServices().values()) {
                Class<?> serviceInterface = service.getServiceContract().getInterfaceClass();
                if (serviceInterface == null) {
                    continue;
                }
                if (requiredInterface.equals(serviceInterface)) {
                    targetUri = URI.create(candidate.getUri().toString() + service.getUri());
                    break;
                } else if (candidateUri == null && requiredInterface.isAssignableFrom(serviceInterface)) {
                    candidateUri = URI.create(candidate.getUri().toString() + service.getUri());
                }
            }
            if (targetUri != null) {
                break;
            }
        }
        if (targetUri == null) {
            targetUri = resolvePrimordial(requiredContract);
        }
        if (candidateUri != null) {
            targetUri = candidateUri;
        }
        if (targetUri != null) {
            target.addTarget(targetUri);
        }
        if (targetUri == null && required) {
            String uri = target.getReferenceName().toString();
            throw new AutowireTargetNotFoundException("No suitable target found for", uri);
        }
    }

    private URI resolvePrimordial(ServiceContract contract) {
        Class<?> requiredClass = contract.getInterfaceClass();
        for (Map.Entry<ServiceContract, URI> entry : hostAutowire.entrySet()) {
            if (requiredClass.isAssignableFrom(entry.getKey().getInterfaceClass())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
