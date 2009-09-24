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

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {
        return processIncludes(composite, monitor);
    }

    /**
     * Merge the elements from the included composites into the including composite
     * @param composite
     * @param monitor
     * @return
     */
    private Composite processIncludes(Composite composite, Monitor monitor) {
        monitor.pushContext("Composite: " + composite.getName().toString());

        try {
            // process any composites referenced through implementation.composite 
            for (Component component : composite.getComponents()) {

                // recurse for composite implementations
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    processIncludes((Composite)implementation, monitor);
                }
            }

            // get the components etc. from any included composites
            for (Composite included : composite.getIncludes()) {
                if (included.isLocal() && !composite.isLocal()) {
                    // ASM60041
                    Monitor.error(monitor, this, "assembly-validation-messages", "IllegalCompositeIncusion", composite
                        .getName().toString(), included.getName().toString());
                    return null;
                }

                // The included has been cloned during composite.clone()
                Composite merged = processIncludes(included, monitor);
                if (merged != null) {
                    for (Component component : merged.getComponents()) {
                        // apply the autowire flag on this composite to any inline 
                        // components - Assembly 5.6 point 4
                        if (component.getAutowire() == null && merged.getAutowire() == Boolean.TRUE) {
                            component.setAutowire(Boolean.TRUE);
                        }
                        // Merge the intents and policySets from the included composite into 
                        // component/service/reference elements under the composite
                        component.getRequiredIntents().addAll(merged.getRequiredIntents());
                        component.getPolicySets().addAll(merged.getPolicySets());
                    }

                    for (Service service : merged.getServices()) {
                        service.getRequiredIntents().addAll(merged.getRequiredIntents());
                        service.getPolicySets().addAll(merged.getPolicySets());
                    }

                    for (Reference reference : merged.getReferences()) {
                        reference.getRequiredIntents().addAll(merged.getRequiredIntents());
                        reference.getPolicySets().addAll(merged.getPolicySets());
                    }
                    composite.getComponents().addAll(merged.getComponents());
                    composite.getServices().addAll(merged.getServices());
                    composite.getReferences().addAll(merged.getReferences());
                    composite.getProperties().addAll(merged.getProperties());
                    composite.getWires().addAll(merged.getWires());
                    // FIXME: What should we do for the extensions
                    /*
                    clone.getExtensions().addAll(fusedComposite.getExtensions());
                    clone.getAttributeExtensions().addAll(fusedComposite.getAttributeExtensions());
                    */
                }
            }

            composite.getIncludes().clear();

            // return the fused composite we have built up so far
            return composite;
        } finally {
            monitor.popContext();
        }
    }
}
