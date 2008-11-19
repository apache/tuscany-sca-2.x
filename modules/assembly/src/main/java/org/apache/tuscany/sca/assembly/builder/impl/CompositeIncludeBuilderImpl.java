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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implementation of a CompositeBuilder.
 *
 * @version $Rev$ $Date$
 */
public class CompositeIncludeBuilderImpl implements CompositeBuilder {   
        
    public CompositeIncludeBuilderImpl(FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
    }
      
    public CompositeIncludeBuilderImpl() {
    }
      
    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeIncludeBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        fuseIncludes(composite, monitor);
    }

    private void warning(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null){
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Collect all includes in a graph of includes.
     * 
     * @param composite
     * @param includes
     */
    private void collectIncludes(Composite composite, List<Composite> includes,
                                 Set<Composite> visited, Monitor monitor) {
        for (Composite include : composite.getIncludes()) {
            if (visited.contains(include)) {
                warning(monitor, "CompositeAlreadyIncluded", composite, include.getName().toString());
                continue;
            }
                        
            includes.add(include);
            visited.add(include);
            collectIncludes(include, includes, visited, monitor);
        }
    }

    /**
     * Copy a list of includes into a composite.
     * 
     * @param composite
     */
    private void fuseIncludes(Composite composite, Monitor monitor) {
    
        // First collect all includes
        List<Composite> includes = new ArrayList<Composite>();
        Set<Composite> visited = new HashSet<Composite>();
        visited.add(composite);
        collectIncludes(composite, includes, visited, monitor);
        
        // Then clone them
        for (Composite include : includes) {
            Composite clone;
            try {
                clone = (Composite)include.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            composite.getComponents().addAll(clone.getComponents());
            composite.getServices().addAll(clone.getServices());
            composite.getReferences().addAll(clone.getReferences());
            composite.getProperties().addAll(clone.getProperties());
            composite.getWires().addAll(clone.getWires());
            composite.getPolicySets().addAll(clone.getPolicySets());
            composite.getRequiredIntents().addAll(clone.getRequiredIntents());
        }
    
        // Clear the list of includes
        composite.getIncludes().clear();
    }

}
