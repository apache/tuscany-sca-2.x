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
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that clones nested composites.
 *
 * @version $Rev$ $Date$
 */
public class CompositeCloneBuilderImpl implements CompositeBuilder {
    
    public CompositeCloneBuilderImpl(Monitor monitor) {
    }
    
    public void build(Composite composite) throws CompositeBuilderException {
        expandCompositeImplementations(composite);
    }

    /**
     * Expand composite component implementations.
     * 
     * @param composite
     * @param problems
     */
    private void expandCompositeImplementations(Composite composite) {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
    
                Composite compositeImplementation = (Composite)implementation;
                Composite clone;
                try {
                    clone = (Composite)compositeImplementation.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                component.setImplementation(clone);
                expandCompositeImplementations(clone);
            }
        }
    }

    /**
     * Collect all nested composite implementations in a graph of composites.
     * 
     * @param composite
     * @param nested
     */
    private void collectNestedComposites(Composite composite, List<Composite> nested) {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                Composite nestedComposite = (Composite)implementation;
                nested.add(nestedComposite);
                collectNestedComposites(nestedComposite, nested);
            }
        }
    }

    /**
     * Fuse nested composites into a top level composite.
     * 
     * @param composite
     */
    private void fuseCompositeImplementations(Composite composite) {
    
        // First collect all nested composites
        List<Composite> nested = new ArrayList<Composite>();
        collectNestedComposites(composite, nested);
    
        // Then add all the non-composite components they contain 
        for (Composite nestedComposite : nested) {
            for (Component component: nestedComposite.getComponents()) {
                Implementation implementation = component.getImplementation();
                if (!(implementation instanceof Composite)) {
                    composite.getComponents().add(component);
                }
            }
        }
    
        // Clear the initial list of composite components
        for (Iterator<Component> i = composite.getComponents().iterator(); i.hasNext();) {
            Component component = i.next();
            if (component.getImplementation() instanceof Composite) {
                i.remove();
            }
        }
    }

}
