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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Implementation of a CompositeBuilder.
 *
 * @version $Rev$ $Date$
 */
public class CompositeIncludeBuilderImpl implements CompositeBuilder {

    public CompositeIncludeBuilderImpl() {
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeIncludeBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        fuseIncludes(composite, monitor);
    }

    /**
     * Copy a list of includes into a composite.
     * 
     * @param composite
     */
    private void fuseIncludes(Composite composite, Monitor monitor) {
        
        monitor.pushContext("Composite: " + composite.getName().toString());
        
        try {
            Set<Composite> visited = new HashSet<Composite>();
            visited.add(composite);
    
            for (Composite included : composite.getIncludes()) {
                if(included.isLocal() && !composite.isLocal()) {
                    // ASM60041
                    Monitor.error(monitor, 
                                    this, 
                                    "assembly-validation-messages", 
                                    "IllegalCompositeIncusion", 
                                    composite.getName().toString(),
                                    included.getName().toString());
                }
                Composite fusedComposite = fuseInclude(included, visited, monitor);
                if (fusedComposite != null) {
                    composite.getComponents().addAll(fusedComposite.getComponents());
                    composite.getServices().addAll(fusedComposite.getServices());
                    composite.getReferences().addAll(fusedComposite.getReferences());
                    composite.getProperties().addAll(fusedComposite.getProperties());
                    composite.getWires().addAll(fusedComposite.getWires());
                }
            }
    
            // Clear the list of includes as all of the included components 
            // have now been added into the top level composite
            composite.getIncludes().clear();
            
            // process any composites referenced through implementation.composite 
            for (Component component : composite.getComponents()) {
                monitor.pushContext("Component: " + component.getName());
                
                try {       
                    // recurse for composite implementations
                    Implementation implementation = component.getImplementation();
                    if (implementation instanceof Composite) {
                        fuseIncludes((Composite)implementation, monitor);
                    }
                } finally {
                    monitor.popContext();
                }
            }             
        
        } finally {
            monitor.popContext();
        }        
    }

    private Composite fuseInclude(Composite include, Set<Composite> visited, Monitor monitor) {

        if (visited.contains(include)) {
            // FIXME: [rfeng] Do we need to have a warning? I think it is fine to have Composite A 
            // include B and C while both B and C include D.
            Monitor.warning(monitor, 
                            this, 
                            "assembly-validation-messages", 
                            "CompositeAlreadyIncluded", 
                            include.getName().toString());
            return null;
        }

        visited.add(include);

        Composite clone;
        try {
            clone = (Composite)include.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        // get the components etc. from any included composites
        for (Composite included : include.getIncludes()) {
            if(included.isLocal() && !include.isLocal()) {
                // ASM60041
                Monitor.error(monitor, 
                                this, 
                                "assembly-validation-messages", 
                                "IllegalCompositeIncusion", 
                                include.getName().toString(),
                                included.getName().toString());
                return null;                    
            }
            
            Composite fusedComposite = fuseInclude(included, visited, monitor);
            if (fusedComposite != null) {
                clone.getComponents().addAll(fusedComposite.getComponents());
                clone.getServices().addAll(fusedComposite.getServices());
                clone.getReferences().addAll(fusedComposite.getReferences());
                clone.getProperties().addAll(fusedComposite.getProperties());
                clone.getWires().addAll(fusedComposite.getWires());
                // FIXME: What should we do for the extensions
                /*
                clone.getExtensions().addAll(fusedComposite.getExtensions());
                clone.getAttributeExtensions().addAll(fusedComposite.getAttributeExtensions());
                */
            }
        }

        // apply the autowire flag on this composite to any inline 
        // components - Assembly 5.6 point 4
        if (include.getAutowire() == Boolean.TRUE) {
            for (Component component : clone.getComponents()) {
                if (component.getAutowire() == null) {
                    component.setAutowire(Boolean.TRUE);
                }
            }
        }
        
        // Merge the intents and policySets from the included composite into 
        // component/service/reference elements under the composite
        for(Component component : clone.getComponents()) {
            component.getRequiredIntents().addAll(include.getRequiredIntents());
            component.getPolicySets().addAll(include.getPolicySets());
        }
        
        for(Service service: clone.getServices()) {
            service.getRequiredIntents().addAll(include.getRequiredIntents());
            service.getPolicySets().addAll(include.getPolicySets());
        }

        for(Reference reference: clone.getReferences()) {
            reference.getRequiredIntents().addAll(include.getRequiredIntents());
            reference.getPolicySets().addAll(include.getPolicySets());
        }
        
        // process any composites referenced through implementation.composite 
        for (Component component : clone.getComponents()) {

            // recurse for composite implementations
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                try {
                    monitor.pushContext("Component: " + component.getName());
                    fuseIncludes((Composite)implementation, monitor);
                } finally {
                    monitor.popContext();
                }
            }
        }
        
        clone.getIncludes().clear();

        // return the fused composite we have built up so far
        return clone;
    }
}
