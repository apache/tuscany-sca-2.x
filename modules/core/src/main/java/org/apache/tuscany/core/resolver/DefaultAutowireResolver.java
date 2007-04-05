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
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.core.wire.IDLMappingService;
import org.apache.tuscany.spi.resolver.ResolutionException;

/**
 * Default implementation of an autowire resolver
 * 
 * @version $Rev$ $Date$
 */
public class DefaultAutowireResolver implements AutowireResolver {
    private Map<ComponentService, URI> hostAutowire = new HashMap<ComponentService, URI>();
    private IDLMappingService mappingService;

    /**
     * @param mappingService
     */
    public DefaultAutowireResolver(IDLMappingService mappingService) {
        super();
        this.mappingService = mappingService;
    }

    @SuppressWarnings( {"unchecked"})
    public void resolve(Composite parentDefinition, Component definition) throws ResolutionException {
        ComponentType type = definition.getImplementation();
        // resolve autowires
        if (type instanceof Composite) {
            Composite compositeType = (Composite)type;
            for (Component child : compositeType.getComponents()) {
                Implementation implementation = child.getImplementation();
                ComponentType childType = implementation;
                if (childType instanceof Composite) {
                    // recurse decendents for composites
                    resolve((Composite)definition, child);
                }
                List<ComponentReference> targets = child.getReferences();
                for (Reference reference : childType.getReferences()) {
                    ComponentReference target = (ComponentReference)getReference(targets, reference.getName());
                    if (target == null) {
                        continue;
                    }
                    if (target.isAutowire()) {
                        boolean required = reference.getMultiplicity() == Multiplicity.ONE_N || reference
                                               .getMultiplicity() == Multiplicity.ONE_ONE;
                        resolve(compositeType, reference, target, required);
                    }
                }
            }
        } else {
            // a leaf level component
            ComponentType componentType = definition.getImplementation();
            List<ComponentReference> targets = definition.getReferences();
            for (Reference reference : componentType.getReferences()) {
                ComponentReference target = getReference(targets, reference.getName());
                if (target == null) {
                    continue;
                }
                if (target.isAutowire()) {
                    Composite ctype = parentDefinition;
                    boolean required = reference.getMultiplicity() == Multiplicity.ONE_N || reference.getMultiplicity() == Multiplicity.ONE_ONE;
                    resolve(ctype, reference, target, required);
                }
            }
        }
    }

    public void addPrimordialService(ComponentService service, URI uri) {
        hostAutowire.put(service, uri);
    }

    private <T extends Reference> T getReference(List<T> refs, String name) {
        for (T ref : refs) {
            if (ref.getName().equals(name)) {
                return ref;
            }
        }
        return null;
    }

    public void resolve(Composite compositeType) throws ResolutionException {
        for (Component child : compositeType.getComponents()) {
            ComponentType childType = child.getImplementation();
            if (childType instanceof Composite) {
                // recurse decendents for composites
                resolve(null, child);
            }
            for (Reference reference : child.getReferences()) {
                Reference target = getReference(childType.getReferences(), reference.getName());
                if (target == null) {
                    continue;
                }
                if (target.isAutowire()) {
                    Contract requiredContract = reference;
                    boolean required = target.getMultiplicity() == Multiplicity.ONE_N || target.getMultiplicity() == Multiplicity.ONE_ONE;
                    resolve(compositeType, requiredContract, target, required);
                }
            }
        }
    }

    /**
     * Performs the actual resolution against a composite TODO this should be
     * extensible allowing for path optimizations
     * 
     * @param composite the composite component type to resolve against
     * @param requiredContract the required target contract
     * @param target the reference target
     * @param required true if the autowire is required
     * @throws AutowireTargetNotFoundException
     */
    private void resolve(Composite composite, Contract requiredContract, Reference target, boolean required)
        throws AutowireTargetNotFoundException {
    
        ComponentService targetService = null;
        // autowire to a target in the parent
        // find a suitable target, starting with components first
        for (Component candidate : composite.getComponents()) {
            ComponentType candidateType = candidate.getImplementation();
            for (Service service : candidateType.getServices()) {
                if(mappingService.isCompatible(requiredContract, service)) {
                    targetService = (ComponentService) service;
                    break;
                }
            }
            if (targetService != null) {
                break;
            }
        }
        if (targetService == null) {
            targetService = resolvePrimordial(requiredContract);
        }
        if (targetService != null) {
            target.getTargets().add(targetService);
        }
        if (targetService == null && required) {
            String uri = target.getName();
            throw new AutowireTargetNotFoundException("No suitable target found for", uri);
        }
    }

    private ComponentService resolvePrimordial(Contract contract) {
        for (Map.Entry<ComponentService, URI> entry : hostAutowire.entrySet()) {
            if (mappingService.isCompatible(contract, entry.getKey())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
