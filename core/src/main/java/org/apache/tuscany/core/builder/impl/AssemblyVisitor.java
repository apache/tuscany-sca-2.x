/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.builder.impl;

import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;

import java.util.List;

/**
 * Decorates an assembly object graph with runtime configurations using a set of builders
 * 
 * @version $Rev$ $Date$
 */
public class AssemblyVisitor implements AssemblyModelVisitor {

    List<ContextFactoryBuilder> builders;

    /**
     * Constructs a visitor
     * 
     * @param builders the collection of builders for creating context factories
     */
    public AssemblyVisitor(List<ContextFactoryBuilder> builders) {
        this.builders = builders;
    }

    /**
     * Initiate walking the object graph
     */
    public boolean start(AssemblyModelObject modelObject) {
        return modelObject.accept(this);
    }

    /**
     * Callback when walking the graph
     */
    public boolean visit(AssemblyModelObject modelObject) {
        for (ContextFactoryBuilder builder : builders) {
            builder.build(modelObject);
        }
        return true;
    }

}
