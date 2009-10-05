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

package org.apache.tuscany.sca.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that clones nested composites.
 *
 * @version $Rev$ $Date$
 */
public class CompositeCloneBuilderImpl implements CompositeBuilder {

    public CompositeCloneBuilderImpl() {
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {

        if (Composite.DOMAIN_COMPOSITE.equals(composite.getName())) {
            // Try to avoid clone for top-level composites that are added to the domain composite
            for (Composite included : composite.getIncludes()) {
                cloneIncludes(included);
                cloneCompositeImplementations(included);
            }
        } else {
            // Clone the includes 
            cloneIncludes(composite);
            cloneCompositeImplementations(composite);
        }

        return composite;
    }

    private void cloneIncludes(Composite composite) {
        List<Composite> includes = new ArrayList<Composite>();
        for (Composite included : composite.getIncludes()) {
            try {
                includes.add((Composite)included.clone());
            } catch (CloneNotSupportedException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        composite.getIncludes().clear();
        composite.getIncludes().addAll(includes);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeCloneBuilder";
    }

    /**
     * Clone composite component implementations
     * 
     * @param composite
     * @param problems
     */
    private void cloneCompositeImplementations(Composite composite) {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                Composite compositeImplementation = (Composite)implementation;
                try {
                    // Please note the clone method is recursive
                    Composite clone = (Composite)compositeImplementation.clone();
                    component.setImplementation(clone);
                } catch (CloneNotSupportedException e) {
                    throw new UnsupportedOperationException(e);
                }
            }
        }
    }

}
