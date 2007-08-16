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
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class CompositeIncludeBuilderImpl {
    
    public CompositeIncludeBuilderImpl(CompositeBuilderMonitor monitor) {
    }

    /**
     * Collect all includes in a graph of includes.
     * 
     * @param composite
     * @param includes
     */
    private void collectIncludes(Composite composite, List<Composite> includes) {
        for (Composite include : composite.getIncludes()) {
            includes.add(include);
            collectIncludes(include, includes);
        }
    }

    /**
     * Copy a list of includes into a composite.
     * 
     * @param composite
     */
    public void fuseIncludes(Composite composite) {
    
        // First collect all includes
        List<Composite> includes = new ArrayList<Composite>();
        collectIncludes(composite, includes);
    
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
            if ( composite instanceof PolicySetAttachPoint ) {
                ((PolicySetAttachPoint)composite).getPolicySets().addAll(((PolicySetAttachPoint)clone).getPolicySets());
                ((PolicySetAttachPoint)composite).getRequiredIntents().addAll(((PolicySetAttachPoint)clone).getRequiredIntents());
            }
        }
    
        // Clear the list of includes
        composite.getIncludes().clear();
    }

}
