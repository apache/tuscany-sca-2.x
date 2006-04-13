/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly;

/**
 * Base interface for all assembly model objects providing methods for managing the model itself.
 */
public interface AssemblyObject {

    /**
     * Initialize this model object.
     * 
     * @param modelContext context providing access to the environment in which this model is being used
     * @throws AssemblyInitializationException if an error ocurrs initializing the artifact
     */
    void initialize(AssemblyContext modelContext) throws AssemblyInitializationException;

    /**
     * Freeze this model object preventing any additional changes.
     */
    void freeze();

    /**
     * Accept a visitor
     * 
     * @param visitor a visitor that is visiting the model
     * @return true if processing is complete and the visitor should stop traversing the model
     */
    boolean accept(AssemblyVisitor visitor);

}
